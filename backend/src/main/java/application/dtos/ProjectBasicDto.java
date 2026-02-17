package application.dtos;

import application.database.entities.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ProjectBasicDto {
    private final UUID project_id;
    private final String name;
    private final String description;

    public ProjectBasicDto(Project project){
        project_id = project.getId();
        name = project.getName();
        description = project.getDescription();
    }
}
