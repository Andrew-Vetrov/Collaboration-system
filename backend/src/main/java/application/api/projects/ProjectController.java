package application.api.projects;

import application.database.entities.Project;
import application.database.services.ProjectService;
import application.dtos.CreateProjectRequest;
import application.dtos.ErrorResponse;
import application.dtos.ProjectBasicDto;
import application.exceptions.NoUserException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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
            @Valid @RequestBody CreateProjectRequest request) throws AuthException {
        UUID userUuid = getCurrentUserId();
        Project project = projectService.createProject(request, userUuid);
        return projectService.convertToBasicDto(project);
    }

    @GetMapping("/projects")
    public List<ProjectBasicDto> getUserProjects() throws AuthException {
        UUID userUuid = getCurrentUserId();
        List<Project> projects = projectService.getUserProjects(userUuid);
        log.info("Found " + projects.size() + " projects for " + userUuid);
        return projects.stream()
                .map(projectService::convertToBasicDto)
                .toList();
    }

    private UUID getCurrentUserId() throws AuthException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()|| "anonymousUser".equals(auth.getPrincipal())) {
            throw new AuthException("User not authenticated");
        }
        
        Object principal = auth.getPrincipal();

        if (principal instanceof Jwt jwt) {
            String userIdStr = jwt.getSubject();
            try {
                UUID ret = UUID.fromString(userIdStr);
                log.info("Found user with id " + ret);
                return ret;
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

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse unauthorizedHandler(AuthException e, HttpServletRequest request) {
        log.warn(e.getMessage());
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(), request.getRequestURI());
    }

}
