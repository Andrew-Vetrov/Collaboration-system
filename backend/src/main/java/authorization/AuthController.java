package authorization;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @Value("${app.frontend.url}")
    private String frontendUrl;

    private final OAuth2AuthorizedClientService authorizedClientService;

    public AuthController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/auth")
    public String auth() {
        return "redirect:/oauth2/authorization/google";
    }

    @GetMapping("/auth/success")
    public String authSuccess(OAuth2AuthenticationToken authentication,
                              @AuthenticationPrincipal OAuth2User principal) {

        String accessToken = getAccessToken(authentication);

        return "redirect:" + frontendUrl + "/auth/success?token=" + encode(accessToken);
    }

    private String getAccessToken(OAuth2AuthenticationToken authentication) {
        if (authentication == null) {
            return null;
        }

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        if (client != null && client.getAccessToken() != null) {
            return client.getAccessToken().getTokenValue();
        }

        return null;
    }

    private String encode(String value) {
        return java.net.URLEncoder.encode(value != null ? value : "",
                java.nio.charset.StandardCharsets.UTF_8);
    }
}