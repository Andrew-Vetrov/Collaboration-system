package application.api.suggestions;

import application.database.entities.Suggestion;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

import static org.hamcrest.Matchers.*;

public class GetProjectSuggestionsTest extends SuggestionBaseClassTest{

    private WebTestClient.ResponseSpec makeGetProjectSuggestionsRequest(UUID projectId, String status, String jwt){
        return webClient.get().uri(uriBuilder -> uriBuilder
                        .path("/project/{projectId}/suggestions")
                        .queryParam("status", status)
                        .build(projectId))
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    // GET /project/{projectId}/suggestions ?status=...

    @Test
    void getProjectSuggestions_allNoFilter() {
        createSuggestion("Idea A", Suggestion.SuggestionStatus.NEW);
        createSuggestion("Idea B", Suggestion.SuggestionStatus.DISCUSSION);
        createSuggestion("Idea C", Suggestion.SuggestionStatus.PLANNED);

        makeGetProjectSuggestionsRequest(testProject.getId(), null, validJwt)
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.data").isArray()
                .jsonPath("$.data.length()").isEqualTo(3)
                .jsonPath("$.data[*].name").value(hasItems("Idea A", "Idea B", "Idea C"))
                .jsonPath("$.data[*].status").value(hasItems("NEW", "DISCUSSION", "PLANNED"))
                .jsonPath("$.data[*].likes").value(everyItem(is(0)));
    }

    @Test
    void getProjectSuggestions_filteredByStatus() {
        createSuggestion("Draft 1", Suggestion.SuggestionStatus.DRAFT);
        createSuggestion("New 1", Suggestion.SuggestionStatus.NEW);
        createSuggestion("New 2", Suggestion.SuggestionStatus.NEW);
        createSuggestion("Rejected", Suggestion.SuggestionStatus.REJECTED);

        makeGetProjectSuggestionsRequest(testProject.getId(), "NEW", validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[*].status").value(everyItem(is("NEW")))
                .jsonPath("$.data[*].name").value(containsInAnyOrder("New 1", "New 2"));
    }

    @Test
    void getProjectSuggestions_invalidStatus() {
        createSuggestion("Accepted one", Suggestion.SuggestionStatus.ACCEPTED);

        makeGetProjectSuggestionsRequest(testProject.getId(), "INVALID", validJwt)
                .expectStatus().isBadRequest();
    }

    @Test
    void getProjectSuggestions_nonExistentProject() {
        UUID fakeProjectId = UUID.randomUUID();

        makeGetProjectSuggestionsRequest(fakeProjectId, null, validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").value(containsString("Project not found"));
    }

    @Test
    void getProjectSuggestions_noAuthorization() {
        UUID projectId = testProject.getId();

        webClient.get()
                .uri("/project/{projectId}/suggestions", projectId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getProjectSuggestions_userWithoutRights() {
        User stranger = User.builder()
                .mail("stranger@example.com")
                .nickname("stranger")
                .build();
        stranger = userRepository.save(stranger);

        String strangerJwt = jwtService.generateToken(stranger.getId());

        makeGetProjectSuggestionsRequest(testProject.getId(), null, strangerJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").value(containsString("has no rights to project"));
    }

    @Test
    void getProjectSuggestions_withLikes() {
        Suggestion s1 = createSuggestion("Popular idea", Suggestion.SuggestionStatus.IN_PROGRESS);
        Suggestion s2 = createSuggestion("Normal idea", Suggestion.SuggestionStatus.PLANNED);

        // Добавляем лайки только к первой идее
        User liker1 = createBlankUser("liker1@example.com");
        User liker2 = createBlankUser("liker2@example.com");

        saveLike(liker1.getId(), s1.getId());
        saveLike(liker2.getId(), s1.getId());

        makeGetProjectSuggestionsRequest(testProject.getId(), null, validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.length()").isEqualTo(2)
                .jsonPath("$.data[0].name").isEqualTo("Popular idea")
                .jsonPath("$.data[0].likes_amount").isEqualTo(2)
                .jsonPath("$.data[1].likes_amount").isEqualTo(0);
    }
}
