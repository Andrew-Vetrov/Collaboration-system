package application.dtos;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostProjectResponse {
    private final ProjectBasicDto project;
}
