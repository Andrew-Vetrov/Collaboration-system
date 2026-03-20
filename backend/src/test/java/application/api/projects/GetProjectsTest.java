package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;
import java.util.UUID;

public class GetProjectsTest extends ProjectBaseClassTest{

    private WebTestClient.ResponseSpec makeGetProjectsRequest(String jwt){
        return webClient.get().uri("/projects")
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    // GET /projects
    @Test
    void apiGetProjects_InvalidUuidInJwt() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeGetProjectsRequest(premadeJwt)
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

        makeGetProjectsRequest(jwt)
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
        makeGetProjectsRequest(validJwt)
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
                .votePeriodStart(ZonedDateTime.now())
                .build();
        Project saved = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(saved)
                .isAdmin(true)
                .votesLeft(1)
                .build();
        projectRightsRepository.save(rights);

        makeGetProjectsRequest(validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.projects").isArray()
                .jsonPath("$.projects.length()").isEqualTo(1)
                .jsonPath("$.projects[0].project_id").isEqualTo(saved.getId().toString())
                .jsonPath("$.projects[0].name").isEqualTo("Test Project")
                .jsonPath("$.projects[0].description").isEqualTo("Description");
    }
}
