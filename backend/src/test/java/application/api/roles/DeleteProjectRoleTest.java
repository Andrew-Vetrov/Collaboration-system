package application.api.roles;

import application.database.entities.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class DeleteProjectRoleTest extends RolesBaseClassTest {

    private WebTestClient.ResponseSpec makeDeleteRoleRequest(UUID projectId, UUID roleId, String jwt) {
        return webClient.delete()
                .uri("/projects/{projectId}/roles/{roleId}", projectId, roleId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    @Test
    void apiDeleteRole_NoToken() {
        webClient.delete()
                .uri("/projects/{projectId}/roles/{roleId}", testProjectId, UUID.randomUUID())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiDeleteRole_InvalidUuidInJwt() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeDeleteRoleRequest(testProjectId, UUID.randomUUID(), premadeJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123");
    }

    @Test
    void apiDeleteRole_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        makeDeleteRoleRequest(testProjectId, UUID.randomUUID(), jwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId);
    }

    @Test
    void apiDeleteRole_NonExistentProject() {
        UUID fakeProjectId = UUID.randomUUID();
        makeDeleteRoleRequest(fakeProjectId, UUID.randomUUID(), validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + fakeProjectId);
    }

    @Test
    void apiDeleteRole_NotAdmin() {
        String otherJwt = jwtService.generateToken(otherUser.getId());
        UUID roleId = createTestRole("RoleToDelete", "#FF0000", 0).getId();
        makeDeleteRoleRequest(testProjectId, roleId, otherJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + otherUser.getId() + " is not an admin of project: " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + roleId);
    }

    @Test
    void apiDeleteRole_RoleNotFound() {
        UUID nonExistentRoleId = UUID.randomUUID();
        makeDeleteRoleRequest(testProjectId, nonExistentRoleId, validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Role not found: " + nonExistentRoleId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + nonExistentRoleId);
    }

    @Test
    void apiDeleteRole_RoleFromDifferentProject() {
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
                .name("OtherRole")
                .color("#000000")
                .build();
        roleInOtherProject = projectRoleRepository.save(roleInOtherProject);


        makeDeleteRoleRequest(testProjectId, roleInOtherProject.getId(), validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Role " + roleInOtherProject.getId() + " does not belong to project " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + roleInOtherProject.getId());
    }

    @Test
    void apiDeleteRole_AssignedToUsers() {
        ProjectRole role = createTestRole("AssignedRole", "#00FF00", 0);
        // Назначаем роль другому пользователю
        UserRole userRole = UserRole.builder()
                .userId(otherUser.getId())
                .projectId(testProjectId)
                .roleId(role.getId())
                .build();
        userRoleRepository.save(userRole);

        makeDeleteRoleRequest(testProjectId, role.getId(), validJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Cannot delete role assigned to users")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles/" + role.getId());
    }

    @Test
    void apiDeleteRole_ValidRequest() {
        ProjectRole role = createTestRole("DeletableRole", "#123456", 0);
        makeDeleteRoleRequest(testProjectId, role.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Роль успешно удалена");

        // Проверяем удаление роли
        assert projectRoleRepository.findById(role.getId()).isEmpty();
    }
}