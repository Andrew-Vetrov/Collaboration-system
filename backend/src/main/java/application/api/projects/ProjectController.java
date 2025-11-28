package application.api.projects;

import application.database.entities.Project;
import application.database.services.ProjectService;
import application.dtos.CreateProjectRequest;
import application.dtos.ErrorResponse;
import application.dtos.ProjectBasicDto;
import application.exceptions.NoUserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
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
        UUID userUuid = UUID.fromString(userId);
        List<Project> projects = projectService.getUserProjects(userUuid);

        return projects.stream()
                .map(projectService::convertToBasicDto)
                .toList();
    }

    @ExceptionHandler(NoUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse noUserHandler(NoUserException e, HttpServletRequest request) {
        log.warn(e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "User not found", request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentHandler(IllegalArgumentException e, HttpServletRequest request) {
        log.warn(e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                "Wrong request parameter", request.getRequestURI());
    }
}
