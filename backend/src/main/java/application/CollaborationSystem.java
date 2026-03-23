package application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@Controller
@EnableAsync
public class CollaborationSystem {

    public static void main(String[] args) {
	System.setProperty("java.netpreferIPv4Stack", "true");
        SpringApplication.run(CollaborationSystem.class, args);
    }

    @GetMapping("/")
    public String index() {
        return "redirect:http://collabsystem.ru/";
    }
}
