package application.database.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "suggestion_tags")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionTag {

    @EmbeddedId
    private SuggestionTagId id;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class SuggestionTagId implements Serializable {

        @Column(name = "suggestion_id", nullable = false)
        private UUID suggestionId;

        @Column(name = "tag_id", nullable = false)
        private UUID tagId;
    }
}