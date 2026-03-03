package application.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SuggestionDto {
    @JsonProperty("suggestion_id")
    private final UUID suggestionId;
    @JsonProperty("uesr_id")
    private final UUID userId;
    @JsonProperty("project_id")
    private final UUID projectId;
    @JsonProperty("placed_at")
    private final ZonedDateTime placedAt;
    @JsonProperty("last_edit")
    private final ZonedDateTime lastEdit;
    @JsonProperty("likes_amount")
    private final long likesAmount;
    private final String name;
    private final String description;
    private final String status;
}
