package application.database.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Duration;
import java.util.UUID;

@Entity
@Table(name = "projects")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "vote_interval", columnDefinition = "INTERVAL")
    @JdbcTypeCode(SqlTypes.INTERVAL_SECOND)
    @Builder.Default
    private Duration voteInterval = Duration.ofDays(7);

    @Column(name = "votes_for_interval")
    @Builder.Default
    private Integer votesForInterval = 1;
}