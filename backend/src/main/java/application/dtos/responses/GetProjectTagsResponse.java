package application.dtos.responses;

import application.dtos.ProjectTagDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetProjectTagsResponse {

    private final List<ProjectTagDto> data;
}