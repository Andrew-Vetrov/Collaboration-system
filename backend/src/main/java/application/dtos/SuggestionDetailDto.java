package application.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class SuggestionDetailDto {

    private final UUID suggestion_id;
    private final UUID user_id;
    private final UUID project_id;
    private final ZonedDateTime placed_at;
    private final ZonedDateTime last_edit;
    private final long likes_amount;
    private final String name;
    private final String description;
    private final String status;
}
