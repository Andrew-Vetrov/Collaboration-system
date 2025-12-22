package application.api.suggestions;

import application.database.entities.Project;
import application.database.entities.Suggestion;
import application.database.services.ProjectService;
import application.database.services.SuggestionService;
import application.dtos.SuggestionDto;
import application.dtos.responses.ErrorResponse;
import application.dtos.responses.GetProjectSuggestionsResponse;
import application.exceptions.NoUserException;
import application.security.JwtService;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SuggestionsController {

    private final JwtService jwtService;
    private final SuggestionService suggestionService;
    private final ProjectService projectService;

    @GetMapping("/project/{projectId}/suggestions")
    public GetProjectSuggestionsResponse getProjectSuggestions(
            @PathVariable("projectId") UUID projectId,
            @RequestParam(required = false) String status) throws AuthException {

        UUID userUuid = jwtService.getCurrentUserId();

        // Проверка доступа: пользователь должен иметь права на проект
        List<Project> userProjects = projectService.getUserProjects(userUuid);
        boolean hasAccess = userProjects.stream()
                .anyMatch(p -> p.getId().equals(projectId));

        if (!hasAccess) {
            throw new AuthException("User " + userUuid + " has no access to project: " + projectId);
        }

        List<Suggestion> suggestions = suggestionService.getSuggestions(projectId, status);

        List<SuggestionDto> dtos = suggestions.stream()
                .map(suggestionService::convertToSuggestionDtoWithLikes)
                .toList();

        log.info("User {} retrieved {} suggestions for project {} (status filter: {})",
                userUuid, dtos.size(), projectId, status != null ? status : "all");

        return new GetProjectSuggestionsResponse(dtos);
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
