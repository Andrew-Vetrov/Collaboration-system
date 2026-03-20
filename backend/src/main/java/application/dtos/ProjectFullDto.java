package application.dtos;

import application.database.entities.Project;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProjectFullDto {
    private final UUID id;
    private final String name;
    private final String description;
    @JsonProperty("vote_interval")
    private final String voteInterval;
    @JsonProperty("votes_for_interval")
    private final int votesForInterval;
    @JsonProperty("voting_period_start")
    private final ZonedDateTime votingPeriodStart;
    @JsonProperty("owner_id")
    private final UUID ownerId;
}