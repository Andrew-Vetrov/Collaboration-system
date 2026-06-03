package application.database.repositories;

import application.database.entities.ProjectTag;
import application.database.entities.SuggestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SuggestionTagRepository extends JpaRepository<SuggestionTag, SuggestionTag.SuggestionTagId> {

    boolean existsByIdTagId(UUID tagId);

    void deleteByIdSuggestionIdAndIdTagId(UUID suggestionId, UUID tagId);

    void deleteAllByIdSuggestionId(UUID suggestionId);

    @Query("SELECT st.id.tagId FROM SuggestionTag st WHERE st.id.suggestionId = :suggestionId")
    List<UUID> findTagIdsBySuggestionId(@Param("suggestionId") UUID suggestionId);

    @Query("SELECT t FROM ProjectTag t JOIN SuggestionTag st ON t.id = st.id.tagId WHERE st.id.suggestionId = :suggestionId")
    List<ProjectTag> findAllTagsBySuggestionId(@Param("suggestionId") UUID suggestionId);
}