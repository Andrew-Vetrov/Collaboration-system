package application.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class TagDto {
    @JsonProperty("tag_id")
    private final UUID tagId;

    private final String name;
    private final String color;
}
