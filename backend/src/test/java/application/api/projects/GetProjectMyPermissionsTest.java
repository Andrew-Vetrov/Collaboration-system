package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class GetProjectMyPermissionsTest extends ProjectBaseClassTest{

    private WebTestClient.ResponseSpec makeGetProjectMyPermissionsRequest(UUID projectId, String jwt){
        return webClient.get().uri("/projects/{projectId}/permissions/me", projectId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
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
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeGetProjectMyPermissionsRequest(projectId, premadeJwt)
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

        makeGetProjectMyPermissionsRequest(projectId, jwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/permissions/me");
    }

    @Test
    void apiGetMyPermissions_NonExistentProject() {
        UUID projectId = UUID.randomUUID();

        makeGetProjectMyPermissionsRequest(projectId, validJwt)
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

        makeGetProjectMyPermissionsRequest(savedProject.getId(), validJwt)
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

        makeGetProjectMyPermissionsRequest(savedProject.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.is_admin").isEqualTo(true)
                .jsonPath("$.likes_remain").isEqualTo(5);
    }

}
