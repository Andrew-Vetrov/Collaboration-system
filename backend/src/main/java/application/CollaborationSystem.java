package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@Controller
@ComponentScan({"authorization", "security"})
public class CollaborationSystem {

    public static void main(String[] args) {
        SpringApplication.run(CollaborationSystem.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "redirect:http://localhost:5173";
    }
}