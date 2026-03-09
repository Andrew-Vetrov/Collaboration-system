package application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateUserPermissionsRequest {
    @JsonProperty("is_admin")
    private boolean isAdmin;
}