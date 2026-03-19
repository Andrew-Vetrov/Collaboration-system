package application.api.suggestions;

import application.database.entities.ProjectRights;
import application.database.entities.Suggestion;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class DeleteProjectSuggestionTest extends SuggestionBaseClassTest {

    private WebTestClient.ResponseSpec deleteProjectSuggestionRequest(UUID suggestionId, String jwt) {
        return webClient.delete()
                .uri("/suggestions/{suggestionId}", suggestionId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    //DELETE /suggestions/{suggestion_id}

    @Test
    void deleteSuggestion_nonAdmin() {
        User member = createBlankUser("member@mail.com");
        projectRightsRepository.save(ProjectRights.builder()
                .userId(member.getId()).project(testProject).isAdmin(false).votesLeft(5).build());

        Suggestion s = createSuggestion("Protected", Suggestion.SuggestionStatus.NEW);
        String memberJwt = jwtService.generateToken(member.getId());

        deleteProjectSuggestionRequest(s.getId(), memberJwt)
                .expectStatus().isForbidden()
                .expectBody().jsonPath("$.error").value(err -> err.toString().contains("Only project admins"));
    }

    @Test
    void deleteSuggestion_nonExistent() {
        deleteProjectSuggestionRequest(UUID.randomUUID(), validJwt)
                .expectStatus().isNotFound();
    }

    @Test
    void deleteSuggestion_noToken() {
        Suggestion s = createSuggestion("No token delete", Suggestion.SuggestionStatus.NEW);

        webClient.delete()
                .uri("/suggestions/{suggestionId}", s.getId())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void deleteSuggestion_valid() {
        Suggestion s = createSuggestion("To delete", Suggestion.SuggestionStatus.NEW);

        deleteProjectSuggestionRequest(s.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Предложение или черновик успешно удалено");
    }
}