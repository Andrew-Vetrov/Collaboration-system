package application.dtos;

import application.database.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProjectUserDto {
    @JsonProperty("user_id")
    private final UUID userId;
    @JsonProperty("email")
    private final String email;
    @JsonProperty("nickname")
    private final String nickname;
    @JsonProperty("avatar_url")
    private final String picture;
    @JsonProperty("is_admin")
    private final boolean isAdmin;

    public ProjectUserDto(User user, boolean is_admin){
        userId = user.getId();
        email = user.getMail();
        nickname = user.getNickname();
        picture = user.getPicture();
        this.isAdmin = is_admin;
    }
}