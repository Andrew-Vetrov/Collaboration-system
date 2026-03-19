package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.UUID;

public class GetProjectSettingsTest extends ProjectBaseClassTest{

    private WebTestClient.ResponseSpec makeGetProjectSettingsRequest(UUID projectId, String jwt){
        return webClient.get().uri("/projects/{projectId}/settings", projectId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    //GET /projects/{projectId}/settings

    @Test
    void apiGetProjectSettings_NoToken() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/settings", projectId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiGetProjectSettings_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeGetProjectSettingsRequest(projectId, premadeJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiGetProjectSettings_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        UUID projectId = UUID.randomUUID();

        makeGetProjectSettingsRequest(projectId, jwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonExistentId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiGetProjectSettings_NonExistentProject() {
        UUID projectId = UUID.randomUUID();

        makeGetProjectSettingsRequest(projectId, validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/settings");
    }

    @Test
    void apiGetProjectSettings_NoAccessToProject() {
        // Создаём проект, но без прав для testUser
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
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        makeGetProjectSettingsRequest(savedProject.getId(), validJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " has no access to project " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/settings");
    }

    @Test
    void apiGetProjectSettings_ValidRequest_intervalHours() {
        // Создаём проект для testUser
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Hours Project")
                .description("Description")
                .voteInterval(Duration.ofHours(30))
                .votesForInterval(10)
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        makeGetProjectSettingsRequest(savedProject.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(project.getId().toString())
                .jsonPath("$.data.name").isEqualTo("Hours Project")
                .jsonPath("$.data.description").isEqualTo("Description")
                .jsonPath("$.data.vote_interval").isEqualTo("30 hours")
                .jsonPath("$.data.votes_for_interval").isEqualTo(10)
                .jsonPath("$.data.owner_id").isEqualTo(testUser.getId().toString());
    }

    @Test
    void apiGetProjectSettings_ValidRequest_intervalDays() {
        // Создаём проект для testUser
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Days Project")
                .description("Description")
                .voteInterval(Duration.ofDays(40))
                .votesForInterval(16)
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        makeGetProjectSettingsRequest(savedProject.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(project.getId().toString())
                .jsonPath("$.data.name").isEqualTo("Days Project")
                .jsonPath("$.data.description").isEqualTo("Description")
                .jsonPath("$.data.vote_interval").isEqualTo("40 days")
                .jsonPath("$.data.votes_for_interval").isEqualTo(16)
                .jsonPath("$.data.owner_id").isEqualTo(testUser.getId().toString());
    }

    @Test
    void apiGetProjectSettings_ValidRequest_intervalMinutes() {
        // Создаём проект для testUser
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Minutes Project")
                .description("Description")
                .voteInterval(Duration.ofMinutes(80))
                .votesForInterval(25)
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        makeGetProjectSettingsRequest(savedProject.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.id").isEqualTo(project.getId().toString())
                .jsonPath("$.data.name").isEqualTo("Minutes Project")
                .jsonPath("$.data.description").isEqualTo("Description")
                .jsonPath("$.data.vote_interval").isEqualTo("80 minutes")
                .jsonPath("$.data.votes_for_interval").isEqualTo(25)
                .jsonPath("$.data.owner_id").isEqualTo(testUser.getId().toString());
    }

}
