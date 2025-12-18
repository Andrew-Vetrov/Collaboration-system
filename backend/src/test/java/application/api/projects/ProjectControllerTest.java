package application.api.projects;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProjectControllerTest {

    @Autowired
    private WebTestClient webClient;
    @Test
    void apiGetProjects_BadUser() throws Exception {
        webClient.get().uri("/projects/123")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("400")
                .jsonPath("$.error").isEqualTo("Wrong request parameter")
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo("/projects/123");
    }

    @Test
    void apiGetProjects_NoSuchUser(@Autowired WebTestClient webClient) throws Exception {
        webClient.get().uri("/projects/267b4f92-09d5-4273-8209-ad337c0")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("400")
                .jsonPath("$.error").isEqualTo("User not found")
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo("/projects/267b4f92-09d5-4273-8209-ad337c0");
    }
}
