package application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class ProjectTagDto {

    @JsonProperty("tag_id")
    private final UUID tagId;

    @JsonProperty("project_id")
    private final UUID projectId;

    private final String name;
    private final String color;
}