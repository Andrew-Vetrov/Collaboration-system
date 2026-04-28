package application.api.roles;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.ProjectRole;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class GetProjectRolesTest extends RolesBaseClassTest {

    private WebTestClient.ResponseSpec makeGetProjectRolesRequest(UUID projectId, String jwt) {
        return webClient.get()
                .uri("/projects/{projectId}/roles", projectId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }
    //GET /projects/{project_id}/roles

    @Test
    void apiGetProjectRoles_NoToken() {
        webClient.get()
                .uri("/projects/{projectId}/roles", testProjectId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiGetProjectRoles_InvalidUuidInJwt() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeGetProjectRolesRequest(testProjectId, premadeJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiGetProjectRoles_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        makeGetProjectRolesRequest(testProjectId, jwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiGetProjectRoles_NonExistentProject() {
        UUID fakeProjectId = UUID.randomUUID();
        makeGetProjectRolesRequest(fakeProjectId, validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + fakeProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + fakeProjectId + "/roles");
    }

    @Test
    void apiGetProjectRoles_NoAccess() {
        Project privateProject = Project.builder()
                .ownerId(otherUser.getId())
                .name("Private")
                .description("desc")
                .votePeriodStart(java.time.ZonedDateTime.now())
                .build();
        privateProject = projectRepository.save(privateProject);
        ProjectRights rights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(privateProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        makeGetProjectRolesRequest(privateProject.getId(), validJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403);
    }

    @Test
    void apiGetProjectRoles_EmptyList() {
        makeGetProjectRolesRequest(testProjectId, validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(0);
    }

    @Test
    void apiGetProjectRoles_ValidRequest() {
        ProjectRole role1 = createTestRole("Role1", "#FF0000", 1);
        ProjectRole role2 = createTestRole("Role2", "#00FF00", 2);

        makeGetProjectRolesRequest(testProjectId, validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].role_id").isEqualTo(role1.getId())
                .jsonPath("$.data[0].project_id").isEqualTo(testProject.getId())
                .jsonPath("$.data[0].name").isEqualTo("Role1")
                .jsonPath("$.data[0].color").isEqualTo("#FF0000")
                .jsonPath("$.data[0].likes_amount").isEqualTo(1)
                .jsonPath("$.data[1].role_id").isEqualTo(role2.getId())
                .jsonPath("$.data[0].project_id").isEqualTo(testProject.getId())
                .jsonPath("$.data[1].name").isEqualTo("Role2")
                .jsonPath("$.data[1].color").isEqualTo("#00FF00")
                .jsonPath("$.data[1].likes_amount").isEqualTo(2);
    }
}