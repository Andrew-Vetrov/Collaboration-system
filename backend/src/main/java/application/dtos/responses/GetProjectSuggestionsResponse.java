package application.dtos.responses;

import application.dtos.SuggestionDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class GetProjectSuggestionsResponse {
    private final List<SuggestionDetailDto> data;
}
