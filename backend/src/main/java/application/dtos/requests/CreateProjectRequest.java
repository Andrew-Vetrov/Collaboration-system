package application.dtos.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateProjectRequest {
    @NotBlank
    private String name;

    private String description;
}
