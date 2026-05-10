package application.database.repositories;

import application.database.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    boolean existsByUser_IdAndProjectRole_Id(UUID userId, UUID roleId);

    Optional<UserRole> findByUser_IdAndProjectRole_Id(UUID userId, UUID roleId);

    long countByProjectRole_Id(UUID roleId);

    List<UserRole> findAllByUser_IdAndProjectRole_ProjectId(UUID userId, UUID projectId);
}