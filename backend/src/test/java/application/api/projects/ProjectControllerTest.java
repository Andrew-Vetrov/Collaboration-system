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
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects");
    }

    @Test
    void apiGetProjects_NoToken(@Autowired WebTestClient webClient) {
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

    // POST /projects

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
    void apiPostProjects_InvalidUuidInJwt() {
        String body = """
                {
                    "name": "New project",
                    "description": "test"
                }
                """;

        webClient.post()
                .uri("/projects")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects");
    }

    @Test
    void apiPostProjects_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);

        String body = """
                {
                    "name": "New project",
                    "description": "test"
                }
                """;

        webClient.post()
                .uri("/projects")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects");
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

    // GET /projects/{projectId}/permissions/me

    @Test
    void apiGetMyPermissions_NoToken() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/permissions/me", projectId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiGetMyPermissions_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/permissions/me", projectId)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/permissions/me");
    }

    @Test
    void apiGetMyPermissions_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        UUID projectId = UUID.randomUUID();

        webClient.get()
                .uri("/projects/{projectId}/permissions/me", projectId)
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/permissions/me");
    }

    @Test
    void apiGetMyPermissions_NonExistentProject() {
        UUID projectId = UUID.randomUUID();

        webClient.get()
                .uri("/projects/{projectId}/permissions/me", projectId)
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/permissions/me");
    }

    @Test
    void apiGetMyPermissions_NoAccessToProject() {
        // Создаём проект, но без прав для testUser
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.get()
                .uri("/projects/{projectId}/permissions/me", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " has no access to project " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/permissions/me");
    }

    @Test
    void apiGetMyPermissions_ValidRequest() {
        // Создаём проект для testUser
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.get()
                .uri("/projects/{projectId}/permissions/me", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.is_admin").isEqualTo(true)
                .jsonPath("$.likes_remain").isEqualTo(5);
    }

    //GET /projects/{projectId}/settings

    @Test
    void apiGetProjectSettings_NoToken() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/settings", projectId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiGetProjectSettings_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiGetProjectSettings_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        UUID projectId = UUID.randomUUID();

        webClient.get()
                .uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiGetProjectSettings_NonExistentProject() {
        UUID projectId = UUID.randomUUID();

        webClient.get()
                .uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiGetProjectSettings_NoAccessToProject() {
        // Создаём проект, но без прав для testUser
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.get()
                .uri("/projects/{projectId}/settings", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " has no access to project " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/settings");
    }

    @Test
    void apiGetProjectSettings_ValidRequest() {
        // Создаём проект для testUser
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .voteInterval(Duration.parse("PT1H"))
                .votesForInterval(10)
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.get()
                .uri("/projects/{projectId}/settings", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody();
    }

    //PUT /projects/{projectId}/settings

    @Test
    void apiPutProjectSettings_NoToken() {
        UUID projectId = UUID.randomUUID();
        webClient.put()
                .uri("/projects/{projectId}/settings", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiPutProjectSettings_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiPutProjectSettings_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        UUID projectId = UUID.randomUUID();

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiPutProjectSettings_NonExistentProject() {
        UUID projectId = UUID.randomUUID();

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiPutProjectSettings_NotAdmin() {
        // Создаём проект, но testUser не admin
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights userRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(false)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(userRights);

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/settings", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " is not an admin of project: " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/settings");
    }

    @Test
    void apiPutProjectSettings_InvalidRequest() {
        // Создаём проект для testUser как admin
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.put()
                .uri("/projects/{projectId}/settings", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Name is not specified")
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/settings");
    }

    @Test
    void apiPutProjectSettings_ValidRequest() {
        // Создаём проект для testUser как admin
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/settings", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Настройки проекта успешно обновлены");
    }

    //GET /projects/{projectId}/users

    @Test
    void apiGetProjectUsers_NoToken() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/users", projectId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiGetProjectUsers_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/users", projectId)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users");
    }

    @Test
    void apiGetProjectUsers_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        UUID projectId = UUID.randomUUID();

        webClient.get()
                .uri("/projects/{projectId}/users", projectId)
                .header("Authorization", "Bearer " + jwt)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users");
    }

    @Test
    void apiGetProjectUsers_NonExistentProject() {
        UUID projectId = UUID.randomUUID();

        webClient.get()
                .uri("/projects/{projectId}/users", projectId)
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users");
    }

    @Test
    void apiGetProjectUsers_NoAccessToProject() {
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.get()
                .uri("/projects/{projectId}/users", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " has no access to project " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users");
    }

    @Test
    void apiGetProjectUsers_ValidRequest() {
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.get()
                .uri("/projects/{projectId}/users", savedProject.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.project_id").isEqualTo(savedProject.getId().toString())
                .jsonPath("$.data.users").isArray()
                .jsonPath("$.data.users.length()").isEqualTo(1)
                .jsonPath("$.data.users[0].user_id").isEqualTo(testUser.getId().toString())
                .jsonPath("$.data.users[0].nickname").isEqualTo("testuser")
                .jsonPath("$.data.users[0].is_admin").isEqualTo(true);
    }

    //DELETE /projects/{projectId}/users/{userId}

    @Test
    void apiDeleteUserFromProject_NoToken() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiDeleteUserFromProject_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users/" + userId);
    }

    @Test
    void apiDeleteUserFromProject_NotAdmin() {
        // Создаём проект, testUser не admin
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights userRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(false)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(userRights);

        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), testUser.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " is not an admin of project: " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + testUser.getId());
    }

    @Test
    void apiDeleteUserFromProject_CannotRemoveAdmin() {
        // Создаём проект, testUser admin, пытаемся удалить другого admin
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights otherRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(otherRights);

        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), otherUser.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("Cannot remove this user from the project")
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + otherUser.getId());
    }

    @Test
    void apiDeleteUserFromProject_CannotRemoveOwner() {
        // Создаём проект, testUser admin, пытаемся удалить owner (себя)
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), testUser.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("Cannot remove this user from the project")
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + testUser.getId());
    }

    @Test
    void apiDeleteUserFromProject_UserNotInProject() {
        // Создаём проект для testUser как admin
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        UUID nonMemberId = UUID.randomUUID();

        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), nonMemberId)
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonMemberId)
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + nonMemberId);
    }

    @Test
    void apiDeleteUserFromProject_ValidRequest() {
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights otherRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(false)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(otherRights);

        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), otherUser.getId())
                .header("Authorization", "Bearer " + validJwt)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Пользователь успешно удален из проекта");
    }

    //PUT /projects/{projectId}/users/{userId}

    @Test
    void apiPutUserPermissions_NoToken() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        webClient.put()
                .uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiPutUserPermissions_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String body = """
                {
                    "is_admin": true
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users/" + userId);
    }

    @Test
    void apiPutUserPermissions_NotOwner() {
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);
        ProjectRights otherRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(otherRights);

        String body = """
                {
                    "is_admin": false
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), testUser.getId())
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User is not an owner of project: " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + testUser.getId());
    }

    @Test
    void apiPutUserPermissions_TargetUserNotInProject() {
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        UUID nonMemberId = UUID.randomUUID();

        String body = """
                {
                    "is_admin": true
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), nonMemberId)
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User " + nonMemberId + " is not a member of project " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + nonMemberId);
    }

    @Test
    void apiPutUserPermissions_ValidRequest() {
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        String body = """
                {
                    "is_admin": false
                }
                """;

        webClient.put()
                .uri("/projects/{projectId}/users/{userId}", savedProject.getId(), testUser.getId())
                .header("Authorization", "Bearer " + validJwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Права пользователя успешно изменены");
    }
}