package application.database.repositories;

import application.database.entities.Suggestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SuggestionRepository extends JpaRepository<Suggestion, UUID> {
    List<Suggestion> findAllByProjectId(UUID projectId);
    List<Suggestion> findAllByProjectIdAndStatus(UUID projectId, String status);
}
