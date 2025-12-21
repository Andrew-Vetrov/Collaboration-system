package application.authorization;

import application.database.services.UserService;
import application.security.JwtService;
import application.database.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.UUID;

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
