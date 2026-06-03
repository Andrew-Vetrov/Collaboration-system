package application.database.repositories;

import application.database.entities.ProjectTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectTagRepository extends JpaRepository<ProjectTag, UUID> {

    List<ProjectTag> findAllByProjectId(UUID projectId);

    boolean existsByProjectIdAndName(UUID projectId, String name);
}