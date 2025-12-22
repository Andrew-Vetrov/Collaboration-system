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

import java.util.UUID;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerTest {
    @Autowired
    private WebTestClient webClient;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectRightsRepository projectRightsRepository;

    private User testUser;
    private String validJwt;

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


    // GET /projects
    @Test
    void apiGetProjects_InvalidUuidInJwt() {
        webClient.get()
                .uri("/projects")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects");
    }

    @Test
    void apiGetProjects_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);

        webClient.get()
                .uri("/projects")
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects");
    }

    @Test
    void apiGetProjects_NoToken(@Autowired WebTestClient webClient) throws Exception {
        webClient.get().uri("/projects")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiGetProjects_ValidUser_NoProjects() {
        webClient.get()
                .uri("/projects")
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projects").isArray()
                .jsonPath("$.projects.length()").isEqualTo(0);
    }

    @Test
    void apiGetProjects_ValidUser_WithProjects() {
        // Создаём проект для тестового пользователя
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project saved = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(saved)
                .isAdmin(true)
                .votesLeft(1)
                .build();
        projectRightsRepository.save(rights);

        webClient.get()
                .uri("/projects")
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projects").isArray()
                .jsonPath("$.projects.length()").isEqualTo(1)
                .jsonPath("$.projects[0].project_id").isEqualTo(saved.getId().toString())
                .jsonPath("$.projects[0].name").isEqualTo("Test Project")
                .jsonPath("$.projects[0].description").isEqualTo("Description");
    }

    // ========== POST /projects ==========

    @Test
    void apiPostProjects_NoToken() {
        webClient.post()
                .uri("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiPostProjects_EmptyName() {
        String body = """
                {
                    "name": "",
                    "description": "desc"
                }
                """;

        webClient.post()
                .uri("/projects")
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest(); // валидация @NotBlank
    }

    @Test
    void apiPostProjects_ValidRequest() {
        String body = """
                {
                    "name": "New project",
                    "description": "test"
                }
                """;

        webClient.post()
                .uri("/projects")
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.project.project_id").exists()
                .jsonPath("$.project.name").isEqualTo("New project")
                .jsonPath("$.project.description").isEqualTo("test");
    }
}
