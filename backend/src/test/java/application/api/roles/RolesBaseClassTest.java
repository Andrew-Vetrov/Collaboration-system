package application.api.roles;

import application.database.entities.*;
import application.database.repositories.*;
import application.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZonedDateTime;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RolesBaseClassTest {

    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected ProjectRightsRepository projectRightsRepository;

    @Autowired
    protected ProjectRoleRepository projectRoleRepository;

    @Autowired
    protected UserRoleRepository userRoleRepository;

    protected User testUser;
    protected String validJwt;
    protected User otherUser;
    protected Project testProject;
    protected UUID testProjectId;

    @BeforeEach
    void setUp() {
        // Очистка таблиц и создание общих сущностей
        userRoleRepository.deleteAll();
        projectRoleRepository.deleteAll();
        projectRightsRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        testUser = User.builder()
                .mail("test@example.com")
                .nickname("testuser")
                .build();
        testUser = userRepository.save(testUser);
        validJwt = jwtService.generateToken(testUser.getId());

        otherUser = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        otherUser = userRepository.save(otherUser);

        testProject = Project.builder() //по совместительству владелец и администратор проекта
                .ownerId(testUser.getId())
                .name("Test Project")
                .description("Description")
                .votePeriodStart(ZonedDateTime.now())
                .voteInterval(java.time.Duration.ofHours(24))
                .votesForInterval(5)
                .build();
        testProject = projectRepository.save(testProject);
        testProjectId = testProject.getId();

        ProjectRights ownerRights = ProjectRights.builder()
                .userId(testUser.getId())
                .project(testProject)
                .isAdmin(true)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(ownerRights);

        ProjectRights otherRights = ProjectRights.builder()
                .userId(otherUser.getId())
                .project(testProject)
                .isAdmin(false)
                .votesLeft(5)
                .build();
        projectRightsRepository.save(otherRights);
    }

    protected ProjectRole createTestRole(String name, String color, int likesAmount) {
        ProjectRole role = ProjectRole.builder()
                .projectId(testProjectId)
                .name(name)
                .color(color)
                .likesAmount(likesAmount)
                .build();
        return projectRoleRepository.save(role);
    }
}