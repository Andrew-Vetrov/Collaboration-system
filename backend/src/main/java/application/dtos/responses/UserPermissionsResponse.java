package application.dtos.responses;

import application.database.entities.ProjectRights;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPermissionsResponse {
    private final boolean is_admin;
    private final int likes_remain;

    public UserPermissionsResponse(ProjectRights rights){
        is_admin = rights.getIsAdmin();
        likes_remain = rights.getVotesLeft();
    }
}