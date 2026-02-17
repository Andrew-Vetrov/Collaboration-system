package application.dtos.responses;

import application.dtos.ProjectFullDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GetProjectSettingsResponse {
    private final ProjectFullDto data;
}