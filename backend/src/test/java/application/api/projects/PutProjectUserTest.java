package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class PutProjectUserTest extends ProjectBaseClassTest{

    private WebTestClient.ResponseSpec makePutProjectUserRequest(UUID projectId, UUID userId, String jwt, String body){
        return webClient.put().uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    //PUT /projects/{projectId}/users/{userId}

    @Test
    void apiPutUserPermissions_NoToken() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        webClient.put()
                .uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiPutUserPermissions_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        String body = """
                {
                    "is_admin": true
                }
                """;

        makePutProjectUserRequest(projectId, userId, premadeJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users/" + userId);
    }

    @Test
    void apiPutUserPermissions_NotOwner() {
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);
        ProjectRights otherRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(otherRights);

        String body = """
                {
                    "is_admin": false
                }
                """;

        makePutProjectUserRequest(savedProject.getId(), testUser.getId(), validJwt, body)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User is not an owner of project: " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + testUser.getId());
    }

    @Test
    void apiPutUserPermissions_TargetUserNotInProject() {
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        UUID nonMemberId = UUID.randomUUID();

        String body = """
                {
                    "is_admin": true
                }
                """;

        makePutProjectUserRequest(savedProject.getId(), nonMemberId, validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User " + nonMemberId + " is not a member of project " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + nonMemberId);
    }

    @Test
    void apiPutUserPermissions_ValidRequest() {
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        String body = """
                {
                    "is_admin": false
                }
                """;

        makePutProjectUserRequest(savedProject.getId(), testUser.getId(), validJwt, body)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Права пользователя успешно изменены");
    }
}
