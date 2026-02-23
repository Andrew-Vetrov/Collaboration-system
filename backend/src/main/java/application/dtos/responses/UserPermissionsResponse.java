package application.dtos.responses;

import application.database.entities.ProjectRights;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserPermissionsResponse {

    @JsonProperty("is_admin")
    private final boolean is_admin;

    @JsonProperty("likes_remain")
    private final int likes_remain;

    public UserPermissionsResponse(ProjectRights rights) {
        this.is_admin     = rights.getIsAdmin();
        this.likes_remain = rights.getVotesLeft();
    }
}