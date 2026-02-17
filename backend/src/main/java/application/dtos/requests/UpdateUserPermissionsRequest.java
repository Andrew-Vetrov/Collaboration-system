package application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateUserPermissionsRequest {
    private boolean is_admin;
}