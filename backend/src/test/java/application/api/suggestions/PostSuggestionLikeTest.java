package application.api.suggestions;

import application.database.entities.ProjectRights;
import application.database.entities.Suggestion;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class PostSuggestionLikeTest extends SuggestionBaseClassTest {

    private WebTestClient.ResponseSpec postSuggestionLikeRequest(UUID suggestionId, String jwt) {
        return webClient.post()
                .uri("/suggestions/{suggestionId}/likes", suggestionId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    //POST /suggestions/{suggestion_id}/likes

    @Test
    void addLike_noVotesLeft() {
        Suggestion suggestion = createSuggestion("No votes", Suggestion.SuggestionStatus.NEW);

        ProjectRights rights = projectRightsRepository.findByUserIdAndProjectId(testUser.getId(), testProject.getId()).orElseThrow();
        rights.setVotesLeft(0);
        projectRightsRepository.save(rights);

        postSuggestionLikeRequest(suggestion.getId(), validJwt)
                .expectStatus().isBadRequest()
                .expectBody().jsonPath("$.error").value(err -> err.toString().contains("has no votes left"));
    }

    @Test
    void addLike_nonExistentSuggestion() {
        UUID fakeId = UUID.randomUUID();

        postSuggestionLikeRequest(fakeId, validJwt)
                .expectStatus().isNotFound()
                .expectBody().jsonPath("$.error").isEqualTo("Suggestion not found: " + fakeId);
    }

    @Test
    void addLike_userWithoutProjectRights() {
        User stranger = createBlankUser("stranger@example.com");
        String strangerJwt = jwtService.generateToken(stranger.getId());

        Suggestion suggestion = createSuggestion("Secret", Suggestion.SuggestionStatus.NEW);

        postSuggestionLikeRequest(suggestion.getId(), strangerJwt)
                .expectStatus().isForbidden()
                .expectBody().jsonPath("$.error").value(err -> err.toString().contains("has no rights to project"));
    }

    @Test
    void addLike_noToken() {
        Suggestion suggestion = createSuggestion("No token", Suggestion.SuggestionStatus.NEW);

        webClient.post()
                .uri("/suggestions/{suggestionId}/likes", suggestion.getId())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void addLike_valid() {
        Suggestion suggestion = createSuggestion("Like test", Suggestion.SuggestionStatus.NEW);

        postSuggestionLikeRequest(suggestion.getId(), validJwt)
                .expectStatus().isCreated()
                .expectBody(String.class).isEqualTo("Реакция добавлена");
    }
}