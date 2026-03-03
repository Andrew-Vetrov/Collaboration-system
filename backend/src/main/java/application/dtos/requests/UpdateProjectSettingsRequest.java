package application.dtos.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class UpdateProjectSettingsRequest {
    private String name;
    private String description;

    @JsonProperty("vote_interval")
    private String voteInterval;

    @JsonProperty("votes_for_interval")
    private Integer votesForInterval;
}