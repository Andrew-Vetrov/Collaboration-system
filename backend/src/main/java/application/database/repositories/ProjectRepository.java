package application.database.repositories;

import application.database.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @Query("SELECT p FROM Project p JOIN ProjectRights pr ON p.id = pr.projectId WHERE pr.userId = :userId")
    List<Project> findByUserIdWithRights(@Param("userId") UUID userId);
}