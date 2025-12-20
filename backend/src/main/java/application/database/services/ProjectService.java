package application.database.services;

import application.database.repositories.UserRepository;
import application.exceptions.NoUserException;
import application.dtos.CreateProjectRequest;
import application.dtos.ProjectBasicDto;
import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.repositories.ProjectRepository;
import application.database.repositories.ProjectRightsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectRightsRepository projectRightsRepository;
    private final UserRepository userRepository;

    @Transactional
    public Project createProject(CreateProjectRequest request, UUID ownerId) throws NoUserException {
        //Проверяем существование такого пользователя
        if(userRepository.findById(ownerId).isEmpty()) {
            throw new NoUserException("User not found: " + ownerId);
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

    public List<Project> getUserProjects(UUID userId) throws NoUserException {
        //Проверяем существование такого пользователя
        if(userRepository.findById(userId).isEmpty()) {
            throw new NoUserException("User not found: " + userId);
        }
        List<ProjectRights> projectRights = projectRightsRepository.findAllByUserId(userId);
        return projectRights.stream()
                .map(ProjectRights::getProject)
                .toList();
    }

    public ProjectBasicDto convertToBasicDto(Project project) {
        return new ProjectBasicDto(
                project.getId(),
                project.getName(),
                project.getDescription()
        );
    }


}
