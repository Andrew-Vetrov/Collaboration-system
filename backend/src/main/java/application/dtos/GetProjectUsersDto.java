package application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class GetProjectUsersDto {
    @JsonProperty("project_id")
    private final UUID projectId;
    private final List<ProjectUserDto> users;
}
