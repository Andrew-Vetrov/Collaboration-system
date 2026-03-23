package application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CommentResponseDto {
    @JsonProperty("comment_id")
    private final UUID commentId;

    @JsonProperty("user_id")
    private final UUID userId;

    @JsonProperty("suggestion_id")
    private final UUID suggestionId;

    @JsonProperty("comment_reply_to_id")
    private final UUID commentReplyToId;

    @JsonProperty("placed_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private final ZonedDateTime placedAt;

    @JsonProperty("last_edit")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private final ZonedDateTime lastEdit;

    private final String text;
}