package application.api.projects;

import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.services.ProjectService;
import application.dtos.*;
import application.dtos.requests.CreateProjectRequest;
import application.dtos.requests.UpdateProjectSettingsRequest;
import application.dtos.requests.UpdateUserPermissionsRequest;
import application.dtos.responses.*;
import application.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
        UUID userId = jwtService.getCurrentUserId();
        Project project = projectService.createProject(request, userId);
        log.debug("User {} created project {}",
                userId, project.getId());
        return new PostProjectResponse(new ProjectBasicDto(project));
    }

    @GetMapping("/projects")
    public GetProjectResponse getUserProjects() throws AuthException {
        UUID userId = jwtService.getCurrentUserId();
        List<Project> projects = projectService.getUserProjects(userId);
        log.debug("User {} retrieved projects list ({} projects)",
                userId, projects.size());
        List<ProjectBasicDto> dtos = projects.stream()
                .map(ProjectBasicDto::new)
                .toList();
        return new GetProjectResponse(dtos);
    }

    @GetMapping("/projects/{projectId}/permissions/me")
    public UserPermissionsResponse getMyPermissions(
            @PathVariable("projectId") UUID projectId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        ProjectRights permissions = projectService.validateAndGetUserProjectAccess(userId, projectId);

        log.debug("User {} retrieved permissions for project {}: isAdmin={}, votesLeft={}",
                userId, projectId, permissions.getIsAdmin(), permissions.getVotesLeft());

        return new UserPermissionsResponse(permissions);
    }

    @GetMapping("/projects/{projectId}/settings")
    public GetProjectSettingsResponse getProjectSettings(
            @PathVariable("projectId") UUID projectId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        projectService.validateAndGetUserProjectAccess(userId, projectId);

        Project project = projectService.getProjectById(projectId);
        log.debug("User {} retrieved settings for project {}", userId, projectId);

        ProjectFullDto projectFullDto = new ProjectFullDto(project);
        return new GetProjectSettingsResponse(projectFullDto);
    }


    @PutMapping("/projects/{projectId}/settings")
    public String updateProjectSettings(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody UpdateProjectSettingsRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();


        projectService.updateProjectSettings(projectId, request, userId);
        log.debug("User {} updated settings for project {}", userId, projectId);

        return "Настройки проекта успешно обновлены";
    }

    @GetMapping("/projects/{projectId}/users")
    public GetProjectUsersResponse getProjectUsers(
            @PathVariable("projectId") UUID projectId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        projectService.validateAndGetUserProjectAccess(userId, projectId);

        List<ProjectUserDto> users = projectService.getProjectUsers(projectId);

        log.debug("User {} retrieved users list for project {} ({} users)",
                userId, projectId, users.size());

        return new GetProjectUsersResponse(new GetProjectUsersDto(projectId, users));
    }

    @DeleteMapping("/projects/{projectId}/users/{userId}")
    public String removeUserFromProject(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId) throws AuthException {

        UUID currentUserId = jwtService.getCurrentUserId();

        projectService.removeUserFromProject(projectId, userId, currentUserId);
        log.debug("User {}  removed user {} from project {}",
                currentUserId, userId, projectId);

        return "Пользователь успешно удален из проекта";
    }

    @PutMapping("/projects/{projectId}/users/{userId}")
    public String updateUserPermissions(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody UpdateUserPermissionsRequest request) throws AuthException {

        UUID currentUserId = jwtService.getCurrentUserId();

        projectService.updateUserPermissions(projectId, userId, request.isAdmin(), currentUserId);
        log.debug("User {} (admin) updated permissions for user {} in project {}: isAdmin={}",
                currentUserId, userId, projectId, request.isAdmin());

        return "Права пользователя успешно изменены";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse illegalArgumentHandler(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("Illegal argument during: {}", request.getRequestURI(), e);
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AuthException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse unauthorizedHandler(AuthException e, HttpServletRequest request) {
        log.warn("Unauthorized request to: {}", request.getRequestURI(), e);
        return new ErrorResponse(HttpStatus.UNAUTHORIZED.value(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse accessDeniedHandler(AccessDeniedException e, HttpServletRequest request) {
        log.warn("Access denied during: {}", request.getRequestURI(), e);
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(),
                e.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundHandler(EntityNotFoundException e, HttpServletRequest request) {
        log.warn("Entity not found during: {}", request.getRequestURI(), e);
        return new ErrorResponse(HttpStatus.NOT_FOUND.value(),
                e.getMessage(), request.getRequestURI());
    }
}
