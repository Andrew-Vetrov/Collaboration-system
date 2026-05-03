package application.database.services;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.repositories.ProjectRepository;
import application.database.repositories.ProjectRightsRepository;
import application.database.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectAccessService {
    private final ProjectRepository projectRepository;
    private final ProjectRightsRepository projectRightsRepository;
    private final UserRepository userRepository;

    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
    }

    public Optional<ProjectRights> getUserProjectRights(UUID userId, UUID projectId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("User not found: " + userId);
        }

        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found: " + projectId);
        }
        return projectRightsRepository.findByUserIdAndProjectId(userId, projectId);
    }

    public ProjectRights validateAndGetUserProjectAccess(UUID userId, UUID projectId) {
        Optional<ProjectRights> userRights = getUserProjectRights(userId, projectId);
        if (userRights.isEmpty()) {
            throw new AccessDeniedException("User " + userId + " has no access to project " + projectId);
        }

        Project project = getProjectById(projectId);
        restartProjectVotingPeriodIfNeeded(project);

        return userRights.get();

    }

    public boolean isUserProjectAdmin(UUID userId, UUID projectId) {
        Optional<ProjectRights> rights = getUserProjectRights(userId, projectId);
        if (rights.isEmpty()) {
            throw new AccessDeniedException("User " + userId + " has no access to project " + projectId);
        }
        return rights.get().getIsAdmin();
    }


    public void restartProjectVotingPeriodIfNeeded(Project project) {
        ZonedDateTime now = ZonedDateTime.now();
        Duration interval = project.getVoteInterval();

        Duration elapsed = Duration.between(project.getVotePeriodStart(), now);
        if (elapsed.compareTo(interval) < 0) {
            return; //Обновлять голоса рано
        }

        // Сколько полных периодов прошло
        long periodsPassed = elapsed.dividedBy(interval);
        ZonedDateTime newStart = project.getVotePeriodStart()
                .plus(interval.multipliedBy(periodsPassed));

        project.setVotePeriodStart(newStart);
        projectRepository.save(project);

        resetProjectVotes(project);

        log.debug("Project {} votes reset. New period start: {}", project.getId(), newStart);
    }

    public void resetProjectVotes(Project project){
        List<ProjectRights> allRights = projectRightsRepository.findAllByProjectId(project.getId());
        for (ProjectRights rights : allRights) {
            rights.setVotesLeft(project.getVotesForInterval());
        }
        projectRightsRepository.saveAll(allRights);
    }
}