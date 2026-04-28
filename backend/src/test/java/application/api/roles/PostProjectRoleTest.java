package application.api.roles;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class PostProjectRoleTest extends RolesBaseClassTest {

    private WebTestClient.ResponseSpec makeCreateRoleRequest(UUID projectId, String jwt, String body) {
        return webClient.post()
                .uri("/projects/{projectId}/roles", projectId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }
    //POST /projects/{project_id}/roles

    @Test
    void apiCreateRole_NoToken() {
        webClient.post()
                .uri("/projects/{projectId}/roles", testProjectId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiCreateRole_InvalidUuidInJwt() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        String body = """
                {
                    "name": "Developer",
                    "color": "#FF0000",
                    "likes_amount": 0
                }
                """;
        makeCreateRoleRequest(testProjectId, premadeJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiCreateRole_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        String body = """
                {
                    "name": "Developer",
                    "color": "#FF0000",
                    "likes_amount": 0
                }
                """;
        makeCreateRoleRequest(testProjectId, jwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiCreateRole_NonExistentProject() {
        UUID fakeProjectId = UUID.randomUUID();
        String body = """
                {
                    "name": "Developer",
                    "color": "#FF0000",
                    "likes_amount": 0
                }
                """;
        makeCreateRoleRequest(fakeProjectId, validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + fakeProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + fakeProjectId + "/roles");
    }

    @Test
    void apiCreateRole_NotAdmin() {
        // Используем JWT другого пользователя (otherUser), который не является админом
        String otherJwt = jwtService.generateToken(otherUser.getId());
        String body = """
                {
                    "name": "Developer",
                    "color": "#FF0000",
                    "likes_amount": 0
                }
                """;
        makeCreateRoleRequest(testProjectId, otherJwt, body)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + otherUser.getId() + " is not an admin of project: " + testProjectId)
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiCreateRole_EmptyName() {
        String body = """
                {
                    "name": "",
                    "color": "#FF0000",
                    "likes_amount": 0
                }
                """;
        makeCreateRoleRequest(testProjectId, validJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Name is not specified")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiCreateRole_EmptyColor() {
        String body = """
                {
                    "name": "Developer",
                    "color": "",
                    "likes_amount": 0
                }
                """;
        makeCreateRoleRequest(testProjectId, validJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Color is not specified")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiCreateRole_NegativeLikes() {
        String body = """
                {
                    "name": "Developer",
                    "color": "#FF0000",
                    "likes_amount": -1
                }
                """;
        makeCreateRoleRequest(testProjectId, validJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Likes amount cannot be negative")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiCreateRole_DuplicateName() {
        createTestRole("Developer", "#FF0000", 0);

        // Пытаемся создать ещё одну с таким же именем
        String body = """
                {
                    "name": "Developer",
                    "color": "#00FF00",
                    "likes_amount": 0
                }
                """;
        makeCreateRoleRequest(testProjectId, validJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Role with such name already exists in project")
                .jsonPath("$.path").isEqualTo("/projects/" + testProjectId + "/roles");
    }

    @Test
    void apiCreateRole_ValidRequest_WithoutLikes() {
        String body = """
                {
                    "name": "Manager",
                    "color": "#0000FF"
                }
                """;
        makeCreateRoleRequest(testProjectId, validJwt, body)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role_id").exists()
                .jsonPath("$.project_id").isEqualTo(testProjectId)
                .jsonPath("$.name").isEqualTo("Manager")
                .jsonPath("$.color").isEqualTo("#0000FF")
                .jsonPath("$.likes_amount").isEqualTo(0);
    }

    @Test
    void apiCreateRole_ValidRequest() {
        String body = """
                {
                    "name": "Lead",
                    "color": "#00FF00",
                    "likes_amount": 10
                }
                """;
        makeCreateRoleRequest(testProjectId, validJwt, body)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.role_id").exists()
                .jsonPath("$.project_id").isEqualTo(testProjectId)
                .jsonPath("$.name").isEqualTo("Lead")
                .jsonPath("$.color").isEqualTo("#00FF00")
                .jsonPath("$.likes_amount").isEqualTo(10);
    }
}