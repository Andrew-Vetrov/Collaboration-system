package application.database.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "suggestion_id", nullable = false)
    private UUID suggestionId;

    @Column(name = "comment_reply_to_id")
    private UUID commentReplyToId;

    @Column(name = "placed_at", nullable = false, updatable = false)
    private ZonedDateTime placedAt = ZonedDateTime.now();

    @Column(name = "last_edit", nullable = false)
    private ZonedDateTime lastEdit = ZonedDateTime.now();

    @Column(nullable = false)
    private String text;
}