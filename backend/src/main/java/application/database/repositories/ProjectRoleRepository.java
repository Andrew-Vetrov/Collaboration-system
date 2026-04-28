package application.database.repositories;

import application.database.entities.ProjectRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRoleRepository extends JpaRepository<ProjectRole, UUID> {

    List<ProjectRole> findAllByProjectId(UUID projectId);

    boolean existsByProjectIdAndName(UUID projectId, String name);
}