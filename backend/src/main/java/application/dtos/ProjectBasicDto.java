package application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class ProjectBasicDto {
    private final UUID projectId;
    private final String name;
    private final String description;
}
