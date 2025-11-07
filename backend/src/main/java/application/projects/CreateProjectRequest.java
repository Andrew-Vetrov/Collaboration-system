package application.projects;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateProjectRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String description;
}