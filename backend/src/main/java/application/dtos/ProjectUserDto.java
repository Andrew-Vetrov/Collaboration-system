package application.dtos;

import application.database.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
public class ProjectUserDto {
    @JsonProperty("user_id")
    private final UUID user_id;
    @JsonProperty("email")
    private final String email;
    @JsonProperty("nickname")
    private final String nickname;
    @JsonProperty("is_admin")
    private final boolean is_admin;

    public ProjectUserDto(User user, boolean is_admin){
        user_id = user.getId();
        email = user.getMail();
        nickname = user.getNickname();
        this.is_admin = is_admin;
    }
}