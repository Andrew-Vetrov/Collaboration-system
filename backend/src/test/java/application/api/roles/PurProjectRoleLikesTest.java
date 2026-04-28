package application.api.roles;

import application.database.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class PurProjectRoleLikesTest extends RolesBaseClassTest {

    private WebTestClient.ResponseSpec makeUpdateLikesRequest(UUID projectId, UUID roleId, String jwt, String body) {
        return webClient.put()
                .uri("/projects/{projectId}/roles/{roleId}/likes", projectId, roleId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    @Test
    void apiUpdateLikes_NoToken() {
        webClient.put()
                .uri("/projects/{projectId}/roles/{roleId}/likes", testProjectId, UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiUpdateLikes_InvalidUuidInJwt() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        String body = """
                {
                    "likes_amount": 10
                }
                """;
        makeUpdateLikesRequest(testProjectId, UUID.randomUUID(), premadeJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123");
    }

    @Test
    void apiUpdateLikes_NonExistentCurrentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        String body = """
                {
                    "likes_amount": 5
                }
                """;
        makeUpdateLikesRequest(testProjectId, UUID.randomUUID(), jwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId);
    }

    @Test
    void apiUpdateLikes_NonExistentProject() {
        UUID fakeProjectId = UUID.randomUUID();
        String body = """
                {
                    "likes_amount": 5
                }
                """;
        makeUpdateLikesRequest(fakeProjectId, UUID.randomUUID(), validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + fakeProjectId);
    }

    @Test
    void apiUpdateLikes_NotAdmin() {
        String otherJwt = jwtService.generateToken(otherUser.getId());
        ProjectRole role = createTestRole("RoleToUpdate", "#FF00FF", 0);
        String body = """
                {
                    "likes_amount": 100
                }
                """;
        makeUpdateLikesRequest(testProjectId, role.getId(), otherJwt, body)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + otherUser.getId() + " is not an admin of project: " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + role.getId() + "/likes");
    }

    @Test
    void apiUpdateLikes_RoleNotFound() {
        UUID nonExistentRoleId = UUID.randomUUID();
        String body = """
                {
                    "likes_amount": 5
                }
                """;
        makeUpdateLikesRequest(testProjectId, nonExistentRoleId, validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Role not found: " + nonExistentRoleId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + nonExistentRoleId + "/likes");
    }

    @Test
    void apiUpdateLikes_RoleFromDifferentProject() {
        Project otherProject = Project.builder()
                .ownerId(otherUser.getId())
                .name("Other")
                .description("desc")
                .votePeriodStart(java.time.ZonedDateTime.now())
                .build();
        otherProject = projectRepository.save(otherProject);
        ProjectRights rights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(otherProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);
        ProjectRole foreignRole = ProjectRole.builder()
                .projectId(otherProject.getId())
                .name("ForeignRole")
                .color("#000000")
                .likesAmount(0)
                .build();
        foreignRole = projectRoleRepository.save(foreignRole);

        String body = """
                {
                    "likes_amount": 50
                }
                """;
        makeUpdateLikesRequest(testProjectId, foreignRole.getId(), validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Role " + foreignRole.getId() + " does not belong to project " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + foreignRole.getId() + "/likes");
    }

    @Test
    void apiUpdateLikes_NegativeAmount() {
        ProjectRole role = createTestRole("NegativeRole", "#111111", 0);
        String body = """
                {
                    "likes_amount": -5
                }
                """;
        makeUpdateLikesRequest(testProjectId, role.getId(), validJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid likes_amount value")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + role.getId() + "/likes");
    }

    @Test
    void apiUpdateLikes_ValidRequest() {
        ProjectRole role = createTestRole("UpdatableRole", "#00AA00", 5);
        String body = """
                {
                    "likes_amount": 42
                }
                """;
        makeUpdateLikesRequest(testProjectId, role.getId(), validJwt, body)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role_id").isEqualTo(role.getId())
                .jsonPath("$.project_id").isEqualTo(testProject.getId())
                .jsonPath("$.name").isEqualTo("UpdatableRole")
                .jsonPath("$.color").isEqualTo("#00AA00")
                .jsonPath("$.likes_amount").isEqualTo(42);

        // Дополнительная проверка, что значение обновилось в БД
        ProjectRole updated = projectRoleRepository.findById(role.getId()).orElseThrow();
        assert updated.getLikesAmount() == 42;
    }
}