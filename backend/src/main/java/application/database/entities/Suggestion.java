package application.database.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnTransformer;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion {
    public enum SuggestionStatus {
        DRAFT,
        NEW,
        DISCUSSION,
        PLANNED,
        IN_PROGRESS,
        ACCEPTED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @Column(name = "placed_at", nullable = false)
    private ZonedDateTime placedAt;

    @Column(name = "last_edit", nullable = false)
    private ZonedDateTime lastEdit;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @ColumnTransformer(
            read = "status::text",
            write = "?::suggestion_status"
    )
    private SuggestionStatus status;
}
