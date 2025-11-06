package application.projects;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectBasicDto {
    private final UUID projectId;
    private final String name;
    private final String description;
}