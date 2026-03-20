package application.database.services;

import application.database.entities.ProjectRights;
import application.database.repositories.ProjectRepository;
import application.database.repositories.ProjectRightsRepository;
import application.database.repositories.UserRepository;
import application.dtos.requests.InviteRequestDto;
import application.dtos.responses.InviteResponseDto;
import application.database.entities.Invite;
import application.database.repositories.InviteRepository;
import application.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InviteService {

    private final InviteRepository inviteRepository;
    private final UserRepository userRepository;
    private final ProjectRepository projectRepository;
    private final ProjectRightsRepository projectRightsRepository;

    private final JwtService jwtService;

    private final InternalResourceViewResolver internalResourceViewResolver;

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
    public void inviteUserToProject(UUID projectId, String userEmail, String senderNickname) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project with ID " + projectId + " does not exist.");
        }

        var user = userRepository.findByMail(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User with email " + userEmail + " does not exist."));

        boolean inviteExists = inviteRepository.findAllByProjectId(projectId).stream()
                .anyMatch(invite -> invite.getEmail().equalsIgnoreCase(userEmail));

        if (inviteExists) {
            throw new IllegalStateException("User has already been invited to this project.");
        }

        Invite invite = Invite.builder()
                .projectId(projectId)
                .email(userEmail)
                .invitedAt(LocalDateTime.now())
                .senderNickname(senderNickname)
                .projectName(projectRepository.findById(projectId).get().getName())
                .receiverNickname(user.getNickname())
                .receiverAvatar("")
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
