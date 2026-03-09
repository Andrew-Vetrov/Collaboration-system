package application.database.repositories;

import application.database.entities.ProjectRights;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProjectRightsRepository extends JpaRepository<ProjectRights, UUID> {
    List<ProjectRights> findAllByUserId(UUID userId);
    List<ProjectRights> findAllByProjectId(UUID projectId);
    Optional<ProjectRights> findByUserIdAndProjectId(UUID userId, UUID projectId);
    boolean existsByUserIdAndProjectId(UUID userId, UUID projectId);
}