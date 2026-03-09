package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

public class PutProjectSettingsTest extends ProjectBaseClassTest{

    private WebTestClient.ResponseSpec makePostProjectSettingsRequest(UUID projectId, String jwt, String body){
        return webClient.put().uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer " + jwt)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange();
    }

    //PUT /projects/{projectId}/settings

    @Test
    void apiPutProjectSettings_NoToken() {
        UUID projectId = UUID.randomUUID();
        webClient.put()
                .uri("/projects/{projectId}/settings", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiPutProjectSettings_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        makePostProjectSettingsRequest(projectId, premadeJwt, body)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiPutProjectSettings_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .build();
        Project savedProject = projectRepository.save(project);

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        makePostProjectSettingsRequest(savedProject.getId(), jwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/settings");
    }

    @Test
    void apiPutProjectSettings_NonExistentProject() {
        UUID projectId = UUID.randomUUID();

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        makePostProjectSettingsRequest(projectId, validJwt, body)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiPutProjectSettings_NotAdmin() {
        // Создаём проект, но testUser не admin
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

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights userRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(false)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(userRights);

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;

        makePostProjectSettingsRequest(savedProject.getId(), validJwt, body)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " is not an admin of project: " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/settings");
    }

    @Test
    void apiPutProjectSettings_InvalidRequest() {
        // Создаём проект для testUser как admin
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


        makePostProjectSettingsRequest(savedProject.getId(), validJwt, "{}")
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Name is not specified")
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/settings");
    }

    @Test
    void apiPutProjectSettings_ValidRequest() {
        // Создаём проект для testUser как admin
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

        String body = """
                {
                    "name": "newname",
                    "description": "Updated Description",
                    "vote_interval": "1 week",
                    "votes_for_interval": 4
                }
                """;


        makePostProjectSettingsRequest(savedProject.getId(), validJwt, body)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Настройки проекта успешно обновлены");
    }

}
