package application.database.services;

import application.database.entities.ProjectRights;
import application.database.repositories.ProjectRepository;
import application.database.repositories.ProjectRightsRepository;
import application.database.repositories.UserRepository;
import application.dtos.ProjectUserDto;
import application.dtos.requests.InviteRequestDto;
import application.dtos.responses.InviteResponseDto;
import application.database.entities.Invite;
import application.database.repositories.InviteRepository;
import application.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRightsRepository projectRightsRepository;
    private final UserService userService;

    private final JwtService jwtService;

    private final InternalResourceViewResolver internalResourceViewResolver;

    @Autowired
    private ProjectService projectService;

    @Value("${spring.mail.unisender}") // Добавь в .env/properties
    private String API_KEY;

    @Async
    public void sendInvite(String recipientEmail, String projectName) {
        try {
            String jsonBody = """
        {
            "mail": {
                "to": {
                    "email": "%s"
                },
                "from": {
                    "email": "invites@collabsystem.ru",
                    "name": "Collaboration System"
                },
                "subject": "[Collaboration System] Новое приглашение",
                "html": "<h2>Привет!</h2><p>Тебя пригласили в проект '%s'. Смотри раздел 'Приглашения' на <a href='https://collabsystem.ru'>сайте</a></p>",
                "text": "Привет! Тебя пригласили в проект: https://collabsystem.ru"
            },
            "idempotencyKey": "%s"
        }
        """.formatted(recipientEmail, projectName, java.util.UUID.randomUUID().toString());

            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(10))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.rusender.ru/api/v1/external-mails/send"))
                    .header("Content-Type", "application/json")
                    .header("X-Api-Key", API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenAccept(res -> {
                        System.out.println("RuSender Status: " + res.statusCode());
                        if (res.statusCode() == 200 || res.statusCode() == 201) {
                            System.out.println("Письмо принято. Ответ: " + res.body());
                        } else {
                            System.err.println("RuSender Error Response: " + res.body());
                        }
                    })
                    .exceptionally(ex -> {
                        System.err.println("Ошибка сети RuSender: " + ex.getMessage());
                        return null;
                    });

        } catch (Exception e) {
            System.err.println("Ошибка формирования запроса: " + e.getMessage());
        }
    }

    //public void sendInvite(String targetEmail) {
//        MimeMessage message = mailSender.createMimeMessage();
//        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");
//
//        helper.setFrom(new InternetAddress("{spring.mail.username}", "Collaboration System"));
//        helper.setTo(targetEmail);
//        helper.setSubject("Приглашение в проект Collaboration System");
//        helper.setText("Привет! Тебя пригласили в проект. Присоединяйся по ссылке: https://collabsystem.ru", true);

//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setFrom(senderEmail);
//        message.setTo(targetEmail);
//        message.setSubject("Приглашение в Collaboration System");
//        message.setText("Привет! Тебя пригласили в проект. Присоединяйся по ссылке: https://collabsystem.ru");
//
//        mailSender.send(message);
    // }

    @Transactional
    public List<InviteResponseDto> getProjectInvites(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with ID " + projectId + " does not exist.");
        }

        return inviteRepository.findAllByProjectId(projectId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public List<InviteResponseDto> getUserInvites(String email) {
        return inviteRepository.findAllByEmail(email)
                .orElse(List.of())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void inviteUserToProject(UUID projectId, String userEmail, String senderNickname) throws AuthException {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with ID " + projectId + " does not exist.");
        }

//        var user = userRepository.findOrCreateByEmail(userEmail)
//                .orElseThrow(() -> new EntityNotFoundException("User with email " + userEmail + " does not exist."));
        var user = userService.findOrCreateByEmail(userEmail, "User is not registered", "");
        var userId = user.getId();
        var currentUserId = jwtService.getCurrentUserId();

        var rights = projectRightsRepository.findByUserIdAndProjectId(currentUserId, projectId);

        var inviteExists = inviteRepository.findAllByProjectId(projectId).stream()
                .anyMatch(invite -> invite.getEmail().equalsIgnoreCase(userEmail));

        var hasUser = projectService.getProjectUsers(projectId, currentUserId)
                .stream()
                .anyMatch(elem -> elem.getUserId().equals(userId));

        if (rights.isEmpty() || !rights.get().getIsAdmin() || hasUser || inviteExists) {
            throw new IllegalStateException("User has already been invited to this project.");
        }

        Invite invite = Invite.builder()
                .projectId(projectId)
                .email(userEmail)
                .invitedAt(LocalDateTime.now())
                .senderNickname(senderNickname)
                .projectName(projectRepository.findById(projectId).get().getName())
                .receiverNickname(user.getNickname())
                .receiverAvatar(user.getPicture())
                .build();
        inviteRepository.save(invite);
    }

    @Transactional
    public void handleInvite(UUID inviteId, boolean accept) throws AuthException {
        Invite invite = inviteRepository.findById(inviteId)
                .orElseThrow(() -> new EntityNotFoundException("Invite not found."));

        if (accept) { //добавить проверку на то, что это не приглашающий
            var project = projectRepository.findById(invite.getProjectId()).get();

            ProjectRights ownerRights = ProjectRights.builder()
                    .userId(jwtService.getCurrentUserId())
                    .project(project)
                    .isAdmin(false)
                    .votesLeft(project.getVotesForInterval())
                    .build();

            projectRightsRepository.save(ownerRights);
        }

        inviteRepository.delete(invite);
    }

    private InviteResponseDto mapToDto(Invite invite) {
        return InviteResponseDto.builder()
                .inviteId(invite.getInviteId())
                .projectId(invite.getProjectId())
                .email(invite.getEmail())
                .invitedAt(invite.getInvitedAt())
                .senderNickname(invite.getSenderNickname())
                .projectName(invite.getProjectName())
                .receiverNickname(invite.getReceiverNickname())
                .receiverAvatar(invite.getReceiverAvatar())
                .build();
    }
}
