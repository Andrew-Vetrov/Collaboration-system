package application.dtos.responses;


import application.dtos.ProjectBasicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostProjectResponse {
    private final ProjectBasicDto project;
}
