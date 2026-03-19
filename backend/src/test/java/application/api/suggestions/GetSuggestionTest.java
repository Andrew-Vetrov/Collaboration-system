package application.api.suggestions;

import application.database.entities.Like;
import application.database.entities.ProjectRights;
import application.database.entities.Suggestion;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class GetSuggestionTest extends SuggestionBaseClassTest{

    private WebTestClient.ResponseSpec makeGetSuggestionRequest(UUID suggestionId, String jwt){
        return webClient.get().uri("/suggestions/{suggestionId}", suggestionId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }
    // GET /suggestions/{suggestionId}

    @Test
    void getSuggestion_valid() {
        Suggestion suggestion = createSuggestion("Improve login page", Suggestion.SuggestionStatus.NEW);
        suggestion = suggestionRepository.save(suggestion);

        makeGetSuggestionRequest(suggestion.getId(), validJwt)
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.suggestion_id").isEqualTo(suggestion.getId().toString())
                .jsonPath("$.name").isEqualTo("Improve login page")
                .jsonPath("$.description").isEqualTo("Test description")
                .jsonPath("$.status").isEqualTo("new")
                .jsonPath("$.likes_amount").isEqualTo(0)
                .jsonPath("$.user_likes_amount").isEqualTo(0)
                .jsonPath("$.project_id").isEqualTo(testProject.getId().toString())
                .jsonPath("$.user_id").isEqualTo(testUser.getId().toString());
    }

    @Test
    void getSuggestion_suggestionFromAnotherUserValid() {
        User otherUser = createBlankUser("otheruser@mail.com");

        ProjectRights rights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(testProject)
                .isAdmin(false)
                .votesLeft(10)
                .build();
        projectRightsRepository.save(rights);

        Suggestion suggestion = createSuggestion("Add export feature", Suggestion.SuggestionStatus.DISCUSSION);

        String otherJwt = jwtService.generateToken(otherUser.getId());

        makeGetSuggestionRequest(suggestion.getId(), otherJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Add export feature")
                .jsonPath("$.status").isEqualTo("discussion");
    }

    @Test
    void getSuggestion_withLikes_shouldReturnCorrectLikesCount() {
        Suggestion suggestion = createSuggestion("Feature with likes", Suggestion.SuggestionStatus.NEW);

        User user1 = createBlankUser("user1@example.com");
        User user2 = createBlankUser("user2@example.com");
        User user3 = createBlankUser("user3@example.com");

        saveLike(testUser.getId(), suggestion.getId());
        saveLike(user2.getId(), suggestion.getId());
        saveLike(user3.getId(), suggestion.getId());

        makeGetSuggestionRequest(suggestion.getId(), validJwt)
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.suggestion_id").isEqualTo(suggestion.getId().toString())
                .jsonPath("$.name").isEqualTo("Feature with likes")
                .jsonPath("$.status").isEqualTo("new")
                .jsonPath("$.likes_amount").isEqualTo(3)
                .jsonPath("$.user_likes_amount").isEqualTo(1)
                .jsonPath("$.likes_amount").isNumber();
    }

    @Test
    void getSuggestion_nonExistentId_shouldReturn404() {
        UUID fakeId = UUID.randomUUID();

        makeGetSuggestionRequest(fakeId, validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Suggestion not found: " + fakeId)
                .jsonPath("$.path").isEqualTo("/suggestions/" + fakeId);
    }

    @Test
    void getSuggestion_noToken_shouldReturn401() {
        UUID suggestionId = UUID.randomUUID();

        webClient.get()
                .uri("/suggestions/{suggestionId}", suggestionId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getSuggestion_userWithoutProjectRights_shouldReturn403() {
        User stranger = User.builder()
                .mail("stranger@example.com")
                .nickname("stranger")
                .build();
        stranger = userRepository.save(stranger);

        Suggestion suggestion = createSuggestion("Restricted idea", Suggestion.SuggestionStatus.PLANNED);
        suggestion = suggestionRepository.save(suggestion);

        String strangerJwt = jwtService.generateToken(stranger.getId());

        makeGetSuggestionRequest(suggestion.getId(), strangerJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").value(err -> err.toString().contains("has no rights to suggestion"));
    }

    @Test
    void getSuggestion_invalidJwt_shouldReturn401() {
        UUID suggestionId = UUID.randomUUID();
        String brokenJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJub3QtdXVpZCIsImlhdCI6MTUxNjIzOTAyMn0.invalidSignature";

        webClient.get()
                .uri("/suggestions/{suggestionId}", suggestionId)
                .header("Authorization", "Bearer " + brokenJwt)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}
