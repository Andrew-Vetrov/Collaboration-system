package application.database.services;

import application.database.entities.User;
import application.database.repositories.UserRepository;
import application.dtos.ProjectUserDto;
import application.dtos.requests.CreateProjectRequest;
import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.repositories.ProjectRepository;
import application.database.repositories.ProjectRightsRepository;
import application.dtos.requests.UpdateProjectSettingsRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRightsRepository projectRightsRepository;
    private final UserRepository userRepository;

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

    public void validateUserProjectAccess(UUID userId, UUID projectId) {
        if (getUserProjectRights(userId, projectId).isEmpty()) {
            throw new AccessDeniedException("User " + userId + " has no access to project " + projectId);
        }
    }

    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found: " + projectId));
    }

    public boolean isUserProjectAdmin(UUID userId, UUID projectId) {
        Optional<ProjectRights> rights = projectRightsRepository.findByUserIdAndProjectId(userId, projectId);
        if (rights.isEmpty()) {
            throw new AccessDeniedException("User " + userId + " has no access to project " + projectId);
        }
        return rights.get().getIsAdmin();
    }

    @Transactional
    public void updateProjectSettings(UUID projectId, UpdateProjectSettingsRequest request) {
        Project project = getProjectById(projectId);

        if (request.getName() != null && !request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is not specified");
        }
        if (request.getVote_interval() != null && !request.getVote_interval().isBlank()) {
            throw new IllegalArgumentException("Vote interval is not specified");
        }
        if (request.getVotes_for_interval() != null) {
            throw new IllegalArgumentException("Votes for interval is not specified");
        }

        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setVoteInterval(Duration.parse(request.getVote_interval()));
        project.setVotesForInterval(request.getVotes_for_interval());

        projectRepository.save(project);
    }

    public List<ProjectUserDto> getProjectUsers(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException("Project not found: " + projectId);
        }

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
    public void removeUserFromProject(UUID projectId, UUID userId) {
        ProjectRights rights = projectRightsRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " is not a member of project " + projectId));

        projectRightsRepository.delete(rights);
    }

    @Transactional
    public void updateUserPermissions(UUID projectId, UUID userId, boolean isAdmin) {
        ProjectRights rights = projectRightsRepository.findByUserIdAndProjectId(userId, projectId)
                .orElseThrow(() -> new EntityNotFoundException("User " + userId + " is not a member of project " + projectId));

        rights.setIsAdmin(isAdmin);
        projectRightsRepository.save(rights);
    }
}
