package application.dtos;

import application.database.entities.ProjectRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class RoleDto {

    @JsonProperty("role_id")
    private final UUID roleId;

    @JsonProperty("project_id")
    private final UUID projectId;

    private final String name;

    private final String color;

    @JsonProperty("likes_amount")
    private final Integer likesAmount;

    public RoleDto(ProjectRole role) {
        this.roleId = role.getId();
        this.projectId = role.getProjectId();
        this.name = role.getName();
        this.color = role.getColor();
        this.likesAmount = role.getLikesAmount();
    }
}