package application.database.services;

import application.database.entities.User;
import application.database.repositories.UserRepository;
import application.dtos.ProjectFullDto;
import application.dtos.ProjectUserDto;
import application.dtos.requests.CreateProjectRequest;
import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.repositories.ProjectRepository;
import application.database.repositories.ProjectRightsRepository;
import application.dtos.requests.UpdateProjectSettingsRequest;
import application.dtos.responses.GetProjectSettingsResponse;
import application.helpers.DurationHelper;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRightsRepository projectRightsRepository;
    private final UserRepository userRepository;
    private final DurationHelper durationHelper = new DurationHelper();

    @Transactional
    public Project createProject(CreateProjectRequest request, UUID ownerId) throws EntityNotFoundException {
        //Проверяем существование такого пользователя
        if (userRepository.findById(ownerId).isEmpty()) {
            throw new EntityNotFoundException("User not found: " + ownerId);
        }

        // Создаем проект
        Project project = Project.builder()
                .ownerId(ownerId)
                .name(request.getName())
                .description(request.getDescription())
                .build();

        Project savedProject = projectRepository.save(project);

        // Создаем права доступа для владельца
        ProjectRights ownerRights = ProjectRights.builder()
                .userId(ownerId)
                .project(project)
                .isAdmin(true)
                .votesLeft(savedProject.getVotesForInterval())
                .build();

        projectRightsRepository.save(ownerRights);

        return savedProject;
    }

    public List<Project> getUserProjects(UUID userId) throws EntityNotFoundException {
        //Проверяем существование такого пользователя
        if (userRepository.findById(userId).isEmpty()) {
            throw new EntityNotFoundException("User not found: " + userId);
        }
        List<ProjectRights> projectRights = projectRightsRepository.findAllByUserId(userId);
        return projectRights.stream()
                .map(ProjectRights::getProject)
                .toList();
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
        return userRights.get();

    }

    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
    }

    public boolean isUserProjectAdmin(UUID userId, UUID projectId) {
        Optional<ProjectRights> rights = getUserProjectRights(userId, projectId);
        if (rights.isEmpty()) {
            throw new AccessDeniedException("User " + userId + " has no access to project " + projectId);
        }
        return rights.get().getIsAdmin();
    }


    @Transactional
    public void updateProjectSettings(UUID projectId, UpdateProjectSettingsRequest request, UUID userId) {
        Project project = getProjectById(projectId);

        if (!isUserProjectAdmin(userId, projectId)) {
            throw new AccessDeniedException("User " + userId + " is not an admin of project: " + projectId);
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is not specified");
        }
        if (request.getVoteInterval() == null || request.getVoteInterval().isBlank()) {
            throw new IllegalArgumentException("Vote interval is not specified");
        }
        if (request.getVotesForInterval() == null) {
            throw new IllegalArgumentException("Votes for interval is not specified");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setVoteInterval(durationHelper.parseDuration(request.getVoteInterval()));
        project.setVotesForInterval(request.getVotesForInterval());

        projectRepository.save(project);
    }

    public List<ProjectUserDto> getProjectUsers(UUID projectId, UUID currentUserId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found: " + projectId);
        }

        validateAndGetUserProjectAccess(currentUserId, projectId);

        List<ProjectRights> rights = projectRightsRepository.findAllByProjectId(projectId);
        List<ProjectUserDto> ans = new ArrayList<>();
        for (ProjectRights right : rights) {
            Optional<User> user = userRepository.findById(right.getUserId());
            if (user.isEmpty()) {
                log.error("User {} is not found during GET from project {}", right.getUserId(), projectId);
                throw new RuntimeException("User " + right.getUserId() + " is not found");
            }
            ans.add(new ProjectUserDto(user.get(), right.getIsAdmin()));
        }
        return ans;
    }

    @Transactional
    public void removeUserFromProject(UUID projectId, UUID targetUserId, UUID currentUserId) {
        if (!isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }

        // Нельзя удалить администратора или создателя проекта
        if (isUserProjectAdmin(targetUserId, projectId)) {
            throw new AccessDeniedException("Cannot remove admin user from the project");
        }
        if (getProjectById(projectId).getOwnerId().equals(targetUserId)) {
            throw new AccessDeniedException("Cannot remove owner from the project");
        }

        ProjectRights rights = projectRightsRepository.findByUserIdAndProjectId(targetUserId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("User " + targetUserId + " is not a member of project " + projectId));

        projectRightsRepository.delete(rights);
    }

    @Transactional
    public void updateUserPermissions(UUID projectId, UUID targetUserId, boolean isAdmin, UUID currentUserId) {
        // Проверяем, что текущий пользователь является создателем проекта
        if (!getProjectById(projectId).getOwnerId().equals(currentUserId)) {
            throw new AccessDeniedException("User is not an owner of project: " + projectId);
        }
        ProjectRights rights = projectRightsRepository.findByUserIdAndProjectId(targetUserId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("User " + targetUserId + " is not a member of project " + projectId));

        rights.setIsAdmin(isAdmin);
        projectRightsRepository.save(rights);
    }

    @Transactional
    public void consumeVote(UUID userId, UUID projectId) {
        ProjectRights rights = validateAndGetUserProjectAccess(userId, projectId);
        if (rights.getVotesLeft() < 1) {
            throw new IllegalArgumentException("User " + userId + " has no votes left in project " + projectId);
        }
        rights.setVotesLeft(rights.getVotesLeft() - 1);
        projectRightsRepository.save(rights);
    }

    @Transactional
    public void restoreVote(UUID userId, UUID projectId) {
        ProjectRights rights = validateAndGetUserProjectAccess(userId, projectId);
        rights.setVotesLeft(rights.getVotesLeft() + 1);
        projectRightsRepository.save(rights);
    }

    public ProjectFullDto getProjectSettings(UUID projectId, UUID currentUserId){
        validateAndGetUserProjectAccess(currentUserId, projectId);

        Project project = getProjectById(projectId);
        return new ProjectFullDto(
                project.getId(),
                project.getName(),
                project.getDescription(),
                durationHelper.formatDuration(project.getVoteInterval()),
                project.getVotesForInterval(),
                project.getOwnerId());
    }
}
