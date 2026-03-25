package application.api.users;

import application.database.entities.User;
import application.database.repositories.ProjectRepository;
import application.database.repositories.UserRepository;
import application.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GetUsersMeTest {
    @Autowired
    protected WebTestClient webClient;

    @Autowired
    protected JwtService jwtService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    private WebTestClient.ResponseSpec makeUsersMeRequest(String jwt){
        return webClient.get().uri("/users/me")
                .header("Authorization", "Bearer " + jwt)
                .exchange();
    }

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void apiUsersMe_InvalidUuid() {
        String premadeJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU";
        makeUsersMeRequest(premadeJwt)
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.path").isEqualTo("/users/me");
    }

    @Test
    void apiUsersMe_NonExistentUser() {
        UUID nonExistentId = UUID.randomUUID();
        String jwt = jwtService.generateToken(nonExistentId);

        makeUsersMeRequest(jwt)
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("User " + nonExistentId + " not found")
                .jsonPath("$.path").isEqualTo("/users/me");
    }

    @Test
    void apiUsersMe_NoToken() {
        webClient.get().uri("/users/me")
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void apiUsersMe_ValidUser() {
        User user = User.builder()
                .mail("other@example.com")
                .nickname("otheruser")
                .build();
        user = userRepository.save(user);
        String jwt = jwtService.generateToken(user.getId());
        makeUsersMeRequest(jwt)
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.user_id").isEqualTo(user.getId())
                .jsonPath("$.email").isEqualTo(user.getMail())
                .jsonPath("$.nickname").isEqualTo(user.getNickname());
    }
}
