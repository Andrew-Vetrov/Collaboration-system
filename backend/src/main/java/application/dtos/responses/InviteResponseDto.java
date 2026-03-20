package application.dtos.responses;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InviteResponseDto {
    private UUID inviteId;
    private UUID projectId;
    private String email;
    private LocalDateTime invitedAt;
    private String senderNickname;
    private String projectName;
    private String receiverNickname;
    private String receiverAvatar;
}
