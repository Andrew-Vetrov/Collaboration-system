package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.User;
import application.database.repositories.ProjectRepository;
import application.database.repositories.ProjectRightsRepository;
import application.database.repositories.UserRepository;
import application.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectBaseClassTest {
    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected ProjectRightsRepository projectRightsRepository;

    protected User testUser;
    protected String validJwt;

    @BeforeEach
    void setUp() {
        // Очищаем связанные таблицы
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
    }
}