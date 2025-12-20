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
        webClient.get().uri("/projects")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJuYW1lIjoiSm9obiBEb2UiLCJhZG1pbiI6dHJ1ZSwiaWF0IjoxNTE2MjM5MDIyfQ.VvqE9VXHDpg3ZLHyemjkqlHHqFvPOUWi6O_kPFQ9bcU")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("400")
                .jsonPath("$.error").isEqualTo("Invalid UUID in JWT subject: 123")
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo("/projects");
    }

    @Test
    void apiGetProjects_NoSuchUser(@Autowired WebTestClient webClient) throws Exception {
        webClient.get().uri("/projects")
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIyNjdiNGY5Mi0wOWQ1LTQyNzMtODIwOS1hZDMzN2MwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.f9EVD9L2-g-7LJzJ6HFsC958LmMpxYHlkb82YndmG0Y")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo("400")
                .jsonPath("$.error").isEqualTo("User not found: 267b4f92-09d5-4273-8209-00000ad337c0")
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo("/projects");
    }

    @Test
    void apiGetProjects_NoToken(@Autowired WebTestClient webClient) throws Exception {
        webClient.get().uri("/projects")
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.status").isEqualTo("401")
                .jsonPath("$.error").isEqualTo("User not authenticated")
                .jsonPath("$.timestamp").exists()
                .jsonPath("$.path").isEqualTo("/projects");
    }
}
