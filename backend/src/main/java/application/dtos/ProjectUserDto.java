package application.dtos;

import application.database.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProjectUserDto {
    @JsonProperty("user_id")
    private final UUID userId;
    private final String email;
    private final String nickname;
    @JsonProperty("avatar_url")
    private final String picture;
    @JsonProperty("is_admin")
    private final boolean isAdmin;
    private final List<RoleDto> roles;
}