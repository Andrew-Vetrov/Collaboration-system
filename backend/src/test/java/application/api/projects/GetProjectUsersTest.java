package application.api.projects;

import application.database.entities.*;
import application.database.repositories.ProjectRoleRepository;
import application.database.repositories.UserRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class GetProjectUsersTest extends ProjectBaseClassTest{

    @Autowired
    protected ProjectRoleRepository projectRoleRepository;
    @Autowired
    protected UserRoleRepository userRoleRepository;

    private WebTestClient.ResponseSpec makeGetProjectUsersRequest(UUID projectId, String jwt){
        return webClient.get().uri("/projects/{projectId}/users", projectId)
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    //GET /projects/{projectId}/users

    @Test
    void apiGetProjectUsers_NoToken() {
        UUID projectId = UUID.randomUUID();
        webClient.get()
                .uri("/projects/{projectId}/users", projectId)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void apiGetProjectUsers_InvalidUuidInJwt() {
        UUID projectId = UUID.randomUUID();
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeGetProjectUsersRequest(projectId, premadeJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users");
    }

    @Test
    void apiGetProjectUsers_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);
        UUID projectId = UUID.randomUUID();

        makeGetProjectUsersRequest(projectId, jwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users");
    }

    @Test
    void apiGetProjectUsers_NonExistentProject() {
        UUID projectId = UUID.randomUUID();


        makeGetProjectUsersRequest(projectId, validJwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Project not found: " + projectId)
                .jsonPath("$.path").isEqualTo("/projects/" + projectId + "/users");
    }

    @Test
    void apiGetProjectUsers_NoAccessToProject() {
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

        ProjectRights rights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(savedProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(rights);

        makeGetProjectUsersRequest(savedProject.getId(), validJwt)
                .expectStatus().isForbidden()
                .expectBody()
                .jsonPath("$.status").isEqualTo(403)
                .jsonPath("$.error").isEqualTo("User " + testUser.getId() + " has no access to project " + savedProject.getId())
                .jsonPath("$.path").isEqualTo("/projects/" + savedProject.getId() + "/users");
    }

    @Test
    void apiGetProjectUsers_ValidRequest_NoRoles() {
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

        makeGetProjectUsersRequest(savedProject.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.project_id").isEqualTo(savedProject.getId().toString())
                .jsonPath("$.data.users").isArray()
                .jsonPath("$.data.users.length()").isEqualTo(1)
                .jsonPath("$.data.users[0].user_id").isEqualTo(testUser.getId().toString())
                .jsonPath("$.data.users[0].nickname").isEqualTo("testuser")
                .jsonPath("$.data.users[0].is_admin").isEqualTo(true)
                .jsonPath("$.data.users[0].roles").isArray()
                .jsonPath("$.data.users[0].roles.length()").isEqualTo(0);
    }

    @Test
    void apiGetProjectUsers_ValidRequest_WithRoles() {
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

        ProjectRole role1 = ProjectRole.builder()
                .projectId(savedProject.getId())
                .name("role1")
                .color("#00FF00")
                .likesAmount(1)
                .build();
        ProjectRole role2 = ProjectRole.builder()
                .projectId(savedProject.getId())
                .name("role2")
                .color("#00FF00")
                .likesAmount(2)
                .build();
        projectRoleRepository.save(role1);
        projectRoleRepository.save(role2);

        UserRole userRole = UserRole.builder()
                .user(testUser)
                .projectRole(role2).build();
        userRoleRepository.save(userRole);
//        List<UserRole> roles = userRoleRepository.findAllByUserIdAndProjectId(testUser.getId(), savedProject.getId());
        makeGetProjectUsersRequest(savedProject.getId(), validJwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.project_id").isEqualTo(savedProject.getId().toString())
                .jsonPath("$.data.users").isArray()
                .jsonPath("$.data.users.length()").isEqualTo(1)
                .jsonPath("$.data.users[0].user_id").isEqualTo(testUser.getId())
                .jsonPath("$.data.users[0].nickname").isEqualTo("testuser")
                .jsonPath("$.data.users[0].is_admin").isEqualTo(true)
                .jsonPath("$.data.users[0].roles").isArray()
                .jsonPath("$.data.users[0].roles.length()").isEqualTo(1)
                .jsonPath("$.data.users[0].roles[0].project_id").isEqualTo(savedProject.getId())
                .jsonPath("$.data.users[0].roles[0].likes_amount").isEqualTo(2);
    }

}
