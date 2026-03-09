package application.dtos;

import application.database.entities.Project;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ProjectBasicDto {
    @JsonProperty("project_id")
    private final UUID projectId;
    private final String name;
    private final String description;

    public ProjectBasicDto(Project project){
        projectId = project.getId();
        name = project.getName();
        description = project.getDescription();
    }
}
