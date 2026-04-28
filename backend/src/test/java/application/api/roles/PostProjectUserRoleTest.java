package application.api.roles;

import application.database.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class PostProjectUserRoleTest extends RolesBaseClassTest {

    private WebTestClient.ResponseSpec makeAssignRoleRequest(UUID projectId, UUID userId, String jwt, String body) {
        return webClient.post()
                .uri("/projects/{projectId}/users/{userId}/roles", projectId, userId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }
    //POST /projects/{project_id}/users/{user_id}/roles

    @Test
    void apiAssignRole_NoToken() {
        webClient.post()
                .uri("/projects/{projectId}/users/{userId}/roles", testProjectId, otherUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiAssignRole_InvalidUuidInJwt() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(UUID.randomUUID());
        makeAssignRoleRequest(testProjectId, otherUser.getId(), premadeJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles");
    }

    @Test
    void apiAssignRole_NonExistentCurrentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(UUID.randomUUID());
        makeAssignRoleRequest(testProjectId, otherUser.getId(), jwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles");
    }

    @Test
    void apiAssignRole_NonExistentProject() {
        UUID fakeProjectId = UUID.randomUUID();
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(UUID.randomUUID());
        makeAssignRoleRequest(fakeProjectId, otherUser.getId(), validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + fakeProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + fakeProjectId + "/users/" + otherUser.getId() + "/roles");
    }

    @Test
    void apiAssignRole_TargetUserNotInProject() {
        User outsider = User.builder()
                .mail("outsider@example.com")
                .nickname("outsider")
                .build();
        outsider = userRepository.save(outsider);
        ProjectRole role = createTestRole("SomeRole", "#FFFFFF", 0);
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(role.getId());
        makeAssignRoleRequest(testProjectId, outsider.getId(), validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User " + outsider.getId() + " is not a member of project " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + outsider.getId() + "/roles");
    }

    @Test
    void apiAssignRole_NotAdmin() {
        String otherJwt = jwtService.generateToken(otherUser.getId());
        ProjectRole role = createTestRole("AdminOnlyRole", "#0000FF", 0);
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(role.getId());

        makeAssignRoleRequest(testProjectId, otherUser.getId(), otherJwt, body)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + otherUser.getId() + " is not an admin of project: " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles");
    }

    @Test
    void apiAssignRole_RoleNotFound() {
        UUID nonExistentRoleId = UUID.randomUUID();
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(nonExistentRoleId);
        makeAssignRoleRequest(testProjectId, otherUser.getId(), validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Role not found: " + nonExistentRoleId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles");
    }

    @Test
    void apiAssignRole_RoleFromDifferentProject() {
        Project otherProject = Project.builder()
                .ownerId(otherUser.getId())
                .name("Other Project")
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
        ProjectRole roleInOtherProject = ProjectRole.builder()
                .projectId(otherProject.getId())
                .name("ForeignRole")
                .color("#FF00FF")
                .build();
        roleInOtherProject = projectRoleRepository.save(roleInOtherProject);

        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(roleInOtherProject.getId());
        makeAssignRoleRequest(testProjectId, otherUser.getId(), validJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Role does not belong to project")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles");
    }

    @Test
    void apiAssignRole_UserAlreadyHasRole() {
        ProjectRole role = createTestRole("DuplicateRole", "#AA00AA", 0);
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(role.getId());
        makeAssignRoleRequest(testProjectId, otherUser.getId(), validJwt, body)
                .expectStatus().isOk();


        makeAssignRoleRequest(testProjectId, otherUser.getId(), validJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("User already has this role")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles");
    }

    @Test
    void apiAssignRole_ValidRequest() {
        ProjectRole role = createTestRole("ValidAssignRole", "#00AA00", 0);
        String body = """
                {
                    "role_id": "%s"
                }
                """.formatted(role.getId());
        makeAssignRoleRequest(testProjectId, otherUser.getId(), validJwt, body)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Роль успешно добавлена пользователю");

        // Проверяем, что связь создалась
        boolean exists = userRoleRepository.existsByUserIdAndRoleId(otherUser.getId(), role.getId());
        assert exists;
    }
}