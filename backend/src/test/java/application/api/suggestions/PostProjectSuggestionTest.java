package application.api.suggestions;

import application.database.entities.ProjectRights;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.UUID;

public class PostProjectSuggestionTest extends SuggestionBaseClassTest {

    private WebTestClient.ResponseSpec postProjectSuggestionRequest(UUID projectId, String body, String jwt) {
        return webClient.post()
                .uri("/project/{projectId}/suggestions", projectId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    //POST /project/{project_id}/suggestions

    @Test
    void createSuggestion_missingName() {
        String body = """
                {
                    "description": "noname"
                }
                """;
        postProjectSuggestionRequest(testProject.getId(), body, validJwt)
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").isEqualTo("Name is required");
    }

    @Test
    void createSuggestion_invalidStatus() {
        String body = """
                {
                    "name": "Bad",
                    "status": "INVALID"
                }
                """;
        postProjectSuggestionRequest(testProject.getId(), body, validJwt)
                .expectStatus().isBadRequest();
    }

    @Test
    void createSuggestion_nonExistentProject4() {
        String body = """
                {
                    "name": "test"
                }
                """;
        postProjectSuggestionRequest(UUID.randomUUID(), body, validJwt)
                .expectStatus().isNotFound();
    }

    @Test
    void createSuggestion_userWithoutRights() {
        User stranger = createBlankUser("stranger@mail.com");
        String strangerJwt = jwtService.generateToken(stranger.getId());
        String body = """
                {
                    "name": "No rights"
                }
                """;

        postProjectSuggestionRequest(testProject.getId(), body, strangerJwt)
                .expectStatus().isForbidden();
    }

    @Test
    void createSuggestion_noToken_shouldReturn401() {
        webClient.post()
                .uri("/project/{projectId}/suggestions", testProject.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("name", "No token"))
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createSuggestion_valid() {
        String body = """
                {
                    "name": "New feature",
                    "description": "test"
                }
                """;

        postProjectSuggestionRequest(testProject.getId(), body, validJwt)
                .expectStatus().isCreated()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.name").isEqualTo("New feature")
                .jsonPath("$.status").isEqualTo("DRAFT")
                .jsonPath("$.likes_amount").isEqualTo(0);
    }
}