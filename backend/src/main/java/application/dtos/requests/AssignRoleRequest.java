package application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;

@Getter
public class AssignRoleRequest {

    @JsonProperty("role_id")
    private UUID roleId;
}