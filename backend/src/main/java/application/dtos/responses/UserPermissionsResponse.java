package application.dtos.responses;

import application.database.entities.ProjectRights;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UserPermissionsResponse {

    @JsonProperty("is_admin")
    private final boolean isAdmin;

    @JsonProperty("likes_remain")
    private final int likesRemain;

    public UserPermissionsResponse(ProjectRights rights) {
        this.isAdmin     = rights.getIsAdmin();
        this.likesRemain = rights.getVotesLeft();
    }
}