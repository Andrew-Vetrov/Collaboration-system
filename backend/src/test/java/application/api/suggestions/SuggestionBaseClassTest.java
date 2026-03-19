package application.api.suggestions;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.Suggestion;
import application.database.entities.User;
import application.database.repositories.*;
import application.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SuggestionBaseClassTest {
    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected SuggestionRepository suggestionRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProjectRightsRepository projectRightsRepository;

    @Autowired
    protected LikeRepository likeRepository;

    protected User testUser;
    protected String validJwt;
    protected Project testProject;
    @BeforeEach
    void setUp() {
        // Очищаем связанные таблицы
        suggestionRepository.deleteAll();
        projectRightsRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        // Создаём тестового пользователя
        testUser = User.builder()
                .mail("test@example.com")
                .nickname("testuser")
                .build();
        testUser = userRepository.save(testUser);

        // Генерируем валидный JWT для этого пользователя
        validJwt = jwtService.generateToken(testUser.getId());

        testProject = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        testProject = projectRepository.save(testProject);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(testProject)
                .isAdmin(true)
                .votesLeft(10)
                .build();
        projectRightsRepository.save(rights);
    }

    protected Suggestion createSuggestion(String name, Suggestion.SuggestionStatus status) {
        Suggestion s = Suggestion.builder()
                .userId(testUser.getId())
                .projectId(testProject.getId())
                .name(name)
                .description("Test description")
                .status(status)
                .placedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .lastEdit(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
        return suggestionRepository.save(s);
    }

    protected User createBlankUser(String mail) {
        User u = User.builder()
                .mail(mail)
                .nickname("asd")
                .build();
        return userRepository.save(u);
    }

    protected void saveLike(UUID userId, UUID suggestionId) {
        application.database.entities.Like like = application.database.entities.Like.builder()
                .userId(userId)
                .suggestionId(suggestionId)
                .placedAt(ZonedDateTime.now(ZoneOffset.UTC))
                .build();
        likeRepository.save(like);
    }
}
