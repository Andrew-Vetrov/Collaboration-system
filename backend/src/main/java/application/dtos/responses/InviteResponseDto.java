package application.dtos.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InviteResponseDto {
    @JsonProperty("invite_id")
    private UUID inviteId;

    @JsonProperty("project_id")
    private UUID projectId;

    @JsonProperty("email")
    private String email;

    @JsonProperty("invited_at")
    private LocalDateTime invitedAt;

    @JsonProperty("sender_nickname")
    private String senderNickname;

    @JsonProperty("project_name")
    private String projectName;

    @JsonProperty("receiver_nickname")
    private String receiverNickname;

    @JsonProperty("receiver_avatar")
    private String receiverAvatar;
}
