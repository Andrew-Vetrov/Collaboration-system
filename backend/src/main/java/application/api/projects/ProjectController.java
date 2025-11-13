package application.api.projects;

import application.database.entities.Project;
import application.database.services.ProjectService;
import application.dtos.CreateProjectRequest;
import application.dtos.ProjectBasicDto;
import application.exceptions.NoUserException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/projects/{userId}")
    public ProjectBasicDto createProject(
            @Valid @RequestBody CreateProjectRequest request,
            @PathVariable String userId) {
        UUID userUuid = UUID.fromString(userId);
        Project project = projectService.createProject(request, userUuid);
        return projectService.convertToBasicDto(project);
    }

    @GetMapping("/projects/{userId}")
    public List<ProjectBasicDto> getUserProjects(
            @PathVariable String userId) {

        try {
            UUID userUuid = UUID.fromString(userId);
            List<Project> projects = projectService.getUserProjects(userUuid);

            return projects.stream()
                    .map(projectService::convertToBasicDto)
                    .toList();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @ExceptionHandler(NoUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String noUserHandler(NoUserException e) {
        log.warn(e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String illegalArgumentHandler(IllegalArgumentException e) {
        log.warn(e.getMessage());
        return e.getMessage();
    }
}
