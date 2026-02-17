package application.dtos.requests;

import lombok.Getter;

@Getter
public class UpdateProjectSettingsRequest {
    private String name;
    private String description;
    private String vote_interval;
    private Integer votes_for_interval;
}