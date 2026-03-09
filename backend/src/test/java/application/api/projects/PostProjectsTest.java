package application.api.projects;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class PostProjectsTest extends ProjectBaseClassTest{

    private WebTestClient.ResponseSpec makePostProjectsRequest(String jwt, String body){
        return webClient.post().uri("/projects")
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
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
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";

        makePostProjectsRequest(premadeJwt, body)
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

        makePostProjectsRequest(jwt, body)
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

        makePostProjectsRequest(validJwt, body)
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

        makePostProjectsRequest(validJwt, body)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.project.project_id").exists()
                .jsonPath("$.project.name").isEqualTo("New project")
                .jsonPath("$.project.description").isEqualTo("test");
    }

}
