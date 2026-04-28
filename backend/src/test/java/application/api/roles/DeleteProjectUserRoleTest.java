package application.api.roles;

import application.database.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class DeleteProjectUserRoleTest extends RolesBaseClassTest {

    private WebTestClient.ResponseSpec makeRemoveRoleRequest(UUID projectId, UUID userId, UUID roleId, String jwt) {
        return webClient.delete()
                .uri("/projects/{projectId}/users/{userId}/roles/{roleId}", projectId, userId, roleId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }
    //DELETE /projects/{project_id}/users/{user_id}/roles/{role_id}

    @Test
    void apiRemoveRole_NoToken() {
        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}/roles/{roleId}", testProjectId, otherUser.getId(), UUID.randomUUID())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiRemoveRole_InvalidUuidInJwt() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeRemoveRoleRequest(testProjectId, otherUser.getId(), UUID.randomUUID(), premadeJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123");
    }

    @Test
    void apiRemoveRole_NonExistentCurrentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        makeRemoveRoleRequest(testProjectId, otherUser.getId(), UUID.randomUUID(), jwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId);
    }

    @Test
    void apiRemoveRole_NonExistentProject() {
        UUID fakeProjectId = UUID.randomUUID();
        makeRemoveRoleRequest(fakeProjectId, otherUser.getId(), UUID.randomUUID(), validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + fakeProjectId);
    }

    @Test
    void apiRemoveRole_TargetUserNotInProject() {
        User outsider = User.builder()
                .mail("outsider@example.com")
                .nickname("outsider")
                .build();
        outsider = userRepository.save(outsider);
        ProjectRole role = createTestRole("SomeRole", "#FFFFFF", 0);
        makeRemoveRoleRequest(testProjectId, outsider.getId(), role.getId(), validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User " + outsider.getId() + " is not a member of project " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + outsider.getId() + "/roles/" + role.getId());
    }

    @Test
    void apiRemoveRole_NotAdmin() {
        String otherJwt = jwtService.generateToken(otherUser.getId());
        ProjectRole role = createTestRole("AdminRole", "#FF0000", 0);
        UserRole userRole = UserRole.builder()
                .userId(otherUser.getId())
                .projectId(testProjectId)
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);

        makeRemoveRoleRequest(testProjectId, otherUser.getId(), role.getId(), otherJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + otherUser.getId() + " is not an admin of project: " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles/" + role.getId());
    }

    @Test
    void apiRemoveRole_UserDoesNotHaveRole() {
        ProjectRole role = createTestRole("UnownedRole", "#00FF00", 0);
        makeRemoveRoleRequest(testProjectId, otherUser.getId(), role.getId(), validJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("User does not have this role")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/users/" + otherUser.getId() + "/roles/" + role.getId());
    }

    @Test
    void apiRemoveRole_ValidRequest() {
        ProjectRole role = createTestRole("RemovableRole", "#123456", 0);
        // Назначаем роль
        UserRole userRole = UserRole.builder()
                .userId(otherUser.getId())
                .projectId(testProjectId)
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);

        makeRemoveRoleRequest(testProjectId, otherUser.getId(), role.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Роль успешно удалена у пользователя");

        // Проверяем, что связь удалена
        boolean exists = userRoleRepository.existsByUserIdAndRoleId(otherUser.getId(), role.getId());
        assert !exists;
    }
}