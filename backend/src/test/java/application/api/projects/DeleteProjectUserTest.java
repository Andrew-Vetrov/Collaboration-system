package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;
import java.util.UUID;

public class DeleteProjectUserTest extends ProjectBaseClassTest{

    private WebTestClient.ResponseSpec makeDeleteProjectUserRequest(UUID projectId, UUID userId, String jwt){
        return webClient.delete().uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    //DELETE /projects/{projectId}/users/{userId}

    @Test
    void apiDeleteUserFromProject_NoToken() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        webClient.delete()
                .uri("/projects/{projectId}/users/{userId}", projectId, userId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiDeleteUserFromProject_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeDeleteProjectUserRequest(projectId, userId, premadeJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users/" + userId);
    }

    @Test
    void apiDeleteUserFromProject_selfDeleting_valid() {
        // Создаём проект, testUser не admin
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(otherUser.getId())
                .name("Test Project")
                .description("Description")
                .votePeriodStart(ZonedDateTime.now())
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

        makeDeleteProjectUserRequest(savedProject.getId(), testUser.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Пользователь успешно удален из проекта");
    }

    @Test
    void apiDeleteUserFromProject_CannotRemoveAdmin() {
        // Создаём проект, testUser admin, пытаемся удалить другого admin
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .votePeriodStart(ZonedDateTime.now())
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights otherRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(otherRights);

        makeDeleteProjectUserRequest(savedProject.getId(), otherUser.getId(), validJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("Cannot remove admin user from the project")
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + otherUser.getId());
    }

    @Test
    void apiDeleteUserFromProject_CannotRemoveOwner() {
        // Создаём проект, testUser admin, пытаемся удалить owner (себя)
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .votePeriodStart(ZonedDateTime.now())
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights rights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        makeDeleteProjectUserRequest(savedProject.getId(), testUser.getId(), validJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("Cannot exit project being an owner")
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + testUser.getId());
    }

    @Test
    void apiDeleteUserFromProject_UserNotInProject() {
        // Создаём проект для testUser как admin
        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .votePeriodStart(ZonedDateTime.now())
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

        makeDeleteProjectUserRequest(savedProject.getId(), nonMemberId, validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User not found: " + nonMemberId)
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users/" + nonMemberId);
    }

    @Test
    void apiDeleteUserFromProject_ValidRequest() {
        User otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        Project project = Project.builder()
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .votePeriodStart(ZonedDateTime.now())
                .build();
        Project savedProject = projectRepository.save(project);

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights otherRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(false)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(otherRights);

        makeDeleteProjectUserRequest(savedProject.getId(), otherUser.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Пользователь успешно удален из проекта");
    }

}
