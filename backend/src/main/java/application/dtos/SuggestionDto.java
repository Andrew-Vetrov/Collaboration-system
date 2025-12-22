package application.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SuggestionDto {
    private final UUID suggestion_id;
    private final UUID user_id;
    private final UUID project_id;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final ZonedDateTime placed_at;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private final ZonedDateTime last_edit;
    private final long likes_amount;
    private final String name;
    private final String description;
    private final String status;
}
