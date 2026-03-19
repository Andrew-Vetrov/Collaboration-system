package application.database.repositories;

import application.database.entities.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LikeRepository extends JpaRepository<Like, UUID> {
    long countBySuggestionId(UUID suggestionId);
    long countByUserIdAndSuggestionId(UUID userId, UUID suggestionId);

    List<Like> findByUserIdAndSuggestionId(UUID userId, UUID suggestionId);
}
