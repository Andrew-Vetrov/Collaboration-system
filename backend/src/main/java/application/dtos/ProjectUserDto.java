package application.dtos;

import application.database.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProjectUserDto {
    private final UUID user_id;
    private final String email;
    private final String nickname;
    private final boolean is_admin;

    public ProjectUserDto(User user, boolean is_admin){
        user_id = user.getId();
        email = user.getMail();
        nickname = user.getNickname();
        this.is_admin = is_admin;
    }
}