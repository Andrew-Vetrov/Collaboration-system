package application.dtos;

import application.database.entities.Project;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProjectFullDto {
    private final UUID id;
    private final String name;
    private final String description;
    private final String vote_interval;
    private final int votes_for_interval;

    public ProjectFullDto(Project project){
        id = project.getId();
        name = project.getName();
        description = project.getDescription();
        vote_interval = project.getVoteInterval().toString();
        votes_for_interval = project.getVotesForInterval();
    }
}