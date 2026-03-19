package application.api.suggestions;

import application.database.entities.Suggestion;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class DeleteSuggestionLikeTest extends SuggestionBaseClassTest {

    private WebTestClient.ResponseSpec deleteSuggestionLikeRequest(UUID suggestionId, String jwt) {
        return webClient.delete()
                .uri("/suggestions/{suggestionId}/likes", suggestionId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    //DELETE /suggestions/{suggestion_id}/likes

    @Test
    void removeLike_noLikeFromThisUser() {
        Suggestion suggestion = createSuggestion("No my like", Suggestion.SuggestionStatus.NEW);

        User other = createBlankUser("other@mail.com");
        saveLike(other.getId(), suggestion.getId());

        deleteSuggestionLikeRequest(suggestion.getId(), validJwt)
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.error").value(err -> err.toString().contains("No like found for user"));
    }

    @Test
    void removeLike_nonExistentSuggestion() {
        UUID fakeId = UUID.randomUUID();
        deleteSuggestionLikeRequest(fakeId, validJwt).expectStatus().isNotFound();
    }

    @Test
    void removeLike_noToken() {
        Suggestion suggestion = createSuggestion("No token delete", Suggestion.SuggestionStatus.NEW);

        webClient.delete()
                .uri("/suggestions/{suggestionId}/likes", suggestion.getId())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void removeLike_valid() {
        Suggestion suggestion = createSuggestion("Remove test", Suggestion.SuggestionStatus.NEW);

        saveLike(testUser.getId(), suggestion.getId());
        saveLike(testUser.getId(), suggestion.getId());

        deleteSuggestionLikeRequest(suggestion.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Реакция удалена");
    }
}