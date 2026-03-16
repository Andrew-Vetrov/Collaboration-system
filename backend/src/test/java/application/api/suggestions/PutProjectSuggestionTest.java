package application.api.suggestions;

import application.database.entities.ProjectRights;
import application.database.entities.Suggestion;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;
import java.util.UUID;

public class PutProjectSuggestionTest extends SuggestionBaseClassTest {

    private WebTestClient.ResponseSpec putProjectSuggestionRequest(UUID suggestionId, String body, String jwt) {
        return webClient.put()
                .uri("/suggestions/{suggestionId}", suggestionId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    //PUT /suggestions/{suggestion_id}

    @Test
    void updateSuggestion_trySetDraft_shouldReturn400() {
        Suggestion s = createSuggestion("Test", Suggestion.SuggestionStatus.NEW);
        String body = """
                {
                    "status": "DRAFT"
                }
                """;

        putProjectSuggestionRequest(s.getId(), body, validJwt)
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").value(err -> err.toString().contains("Cannot change status to DRAFT"));
    }

    @Test
    void updateSuggestion_notAuthor() {
        User other = createBlankUser("other@mail.com");
        projectRightsRepository.save(ProjectRights.builder()
                .userId(other.getId()).project(testProject).isAdmin(false).votesLeft(5).build());

        Suggestion mySuggestion = createSuggestion("Mine", Suggestion.SuggestionStatus.NEW);
        String otherJwt = jwtService.generateToken(other.getId());
        String body = """
                {
                    "name": "renamed"
                }
                """;

        putProjectSuggestionRequest(mySuggestion.getId(), body, otherJwt)
                .expectStatus().isForbidden()
                .expectBody().jsonPath("$.error").value(err -> err.toString().contains("Only the author"));
    }

    @Test
    void updateSuggestion_nonExistent() {
        String body = """
                {
                    "name": "no"
                }
                """;
        putProjectSuggestionRequest(UUID.randomUUID(), body, validJwt)
                .expectStatus().isNotFound();
    }

    @Test
    void updateSuggestion_valid() {
        Suggestion draft = createSuggestion("Old name", Suggestion.SuggestionStatus.DRAFT);
        String body = """
                {
                    "name": "New name",
                    "status": "new"
                }
                """;

        putProjectSuggestionRequest(draft.getId(), body, validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("New name")
                .jsonPath("$.status").isEqualTo("new");
    }
}