package application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class CreateAndUpdateSuggestionRequest {
    private String name;
    private String description;
    private String status;
    @JsonProperty("tag_ids")
    private List<UUID> tagIds;
}