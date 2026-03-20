package application.database.repositories;

import application.database.entities.Invite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InviteRepository extends JpaRepository<Invite, UUID> {
    List<Invite> findAllByProjectId(UUID projectId);
    Optional<List<Invite>> findAllByEmail(String email);
}
