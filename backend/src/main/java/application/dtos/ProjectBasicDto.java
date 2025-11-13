package application.dtos;

import lombok.AllArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
public class ProjectBasicDto {
    private final UUID projectId;
    private final String name;
    private final String description;
}
