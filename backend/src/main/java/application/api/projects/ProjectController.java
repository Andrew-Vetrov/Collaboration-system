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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/projects")
    public ProjectBasicDto createProject(
            @Valid @RequestBody CreateProjectRequest request) {
        UUID userUuid = getCurrentUserId();
        Project project = projectService.createProject(request, userUuid);
        return projectService.convertToBasicDto(project);
    }

    @GetMapping("/projects")
    public List<ProjectBasicDto> getUserProjects() {
        UUID userUuid = getCurrentUserId();
        List<Project> projects = projectService.getUserProjects(userUuid);

        return projects.stream()
                .map(projectService::convertToBasicDto)
                .toList();
    }

    private UUID getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new NoUserException("User not authenticated");
        }
        // Если в principal положили UUID
        Object principal = auth.getPrincipal();

        // Случай oauth2ResourceServer с JWT
        if (principal instanceof Jwt jwt) {
            String userIdStr = jwt.getSubject(); // или jwt.getClaim("sub")
            try {
                return UUID.fromString(userIdStr);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid UUID in JWT subject: " + userIdStr);
            }
        }
        else {
            throw new IllegalArgumentException("Unsupported principal type: " + principal.getClass());
        }
    }

    @ExceptionHandler(NoUserException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse noUserHandler(NoUserException e, HttpServletRequest request) {
        log.warn(e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentHandler(IllegalArgumentException e, HttpServletRequest request) {
        log.warn(e.getMessage());
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), request.getRequestURI());
    }
}
