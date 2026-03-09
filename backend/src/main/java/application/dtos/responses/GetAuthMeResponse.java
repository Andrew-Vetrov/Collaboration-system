package application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetAuthMeResponse {
    @JsonProperty("user_id")
    private final UUID userId;
    @JsonProperty("email")
    private final String email;
    @JsonProperty("nickname")
    private final String nickname;
    @JsonProperty("avatar_url")
    private final String avatarUrl;
}
