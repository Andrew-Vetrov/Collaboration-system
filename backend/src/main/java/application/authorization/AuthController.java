package application.authorization;

import application.database.entities.Project;
import application.database.services.UserService;
import application.dtos.ProjectBasicDto;
import application.dtos.responses.ErrorResponse;
import application.dtos.responses.GetAuthMeResponse;
import application.dtos.responses.GetProjectResponse;
import application.security.JwtService;
import application.database.entities.User;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

//@RestController
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final JwtService jwtService;
    private final UserService userService;
    private final OAuth2AuthorizedClientService authorizedClientService;

    @GetMapping("/auth")
    public String auth() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/auth/success")
    public String authSuccess(@AuthenticationPrincipal OAuth2User principal) {
        if (principal == null || principal.getAttribute("email") == null) {
            log.error("OAuth2 principal is null or missing email");
        }

        String email = principal.getAttribute("email");

        //Взаимодействуем с бд
        User user = userService.findOrCreateByEmail(email);
        UUID userUuid = user.getId();

        String myJwt = jwtService.generateToken(userUuid);

        log.info("JWT generated with UUID:" + userUuid);
        return "redirect:" + frontendUrl + "/auth/success?token=" + encode(myJwt);
    }

    private String encode(String value) {
        return java.net.URLEncoder.encode(value != null ? value : "",
                java.nio.charset.StandardCharsets.UTF_8);
    }
}
