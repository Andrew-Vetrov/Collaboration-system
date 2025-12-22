package application.api.projects;

import application.database.entities.Project;
import application.database.services.ProjectService;
import application.dtos.*;
import application.exceptions.NoUserException;
import application.security.JwtService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final JwtService jwtService;
    private final ProjectService projectService;

    @PostMapping("/projects")
    public PostProjectResponse createProject(
            @Valid @RequestBody CreateProjectRequest request) throws AuthException {
        UUID userUuid = jwtService.getCurrentUserId();
        Project project = projectService.createProject(request, userUuid);
        return new PostProjectResponse(projectService.convertToBasicDto(project));
    }

    @GetMapping("/projects")
    public GetProjectResponse getUserProjects() throws AuthException {
        UUID userUuid = jwtService.getCurrentUserId();
        List<Project> projects = projectService.getUserProjects(userUuid);
        log.info("Found " + projects.size() + " projects for " + userUuid);
        List<ProjectBasicDto> dtos = projects.stream()
                .map(projectService::convertToBasicDto)
                .toList();
        return new GetProjectResponse(dtos);
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
