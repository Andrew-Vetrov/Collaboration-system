package application.dtos.responses;

import application.dtos.SuggestionDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetProjectSuggestionsResponse {
    private final List<SuggestionDto> data;
}
