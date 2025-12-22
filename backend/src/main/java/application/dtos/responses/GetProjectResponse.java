package application.dtos.responses;


import application.dtos.ProjectBasicDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetProjectResponse {
    private final List<ProjectBasicDto> projects;
}
