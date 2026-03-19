package application.dtos.requests;

import lombok.Getter;

@Getter
public class CreateAndUpdateSuggestionRequest {
    private String name;
    private String description;
    private String status;
}