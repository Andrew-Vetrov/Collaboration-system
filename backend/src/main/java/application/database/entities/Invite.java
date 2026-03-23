package application.database.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "project_invites",
        uniqueConstraints = @UniqueConstraint(columnNames = {"project_id", "email"})
)
public class Invite {

    @Id
    @GeneratedValue
    @Column(name = "invite_id", nullable = false, unique = true)
    private UUID inviteId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "invited_at", nullable = false)
    private LocalDateTime invitedAt;

    @Column(name = "sender_nickname", nullable = false)
    private String senderNickname;

    @Column(name = "project_name", nullable = true)
    private String projectName;

    @Column(name = "receiver_nickname", nullable = false)
    private String receiverNickname;

    @Column(name = "receiver_avatar")
    private String receiverAvatar;
}