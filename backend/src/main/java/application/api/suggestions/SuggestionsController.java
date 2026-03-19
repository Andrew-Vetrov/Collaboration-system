package application.api.suggestions;

import application.database.entities.Project;
import application.database.entities.Suggestion;
import application.database.services.ProjectService;
import application.database.services.SuggestionService;
import application.dtos.SuggestionDetailDto;
import application.dtos.requests.CreateAndUpdateSuggestionRequest;
import application.dtos.responses.ErrorResponse;
import application.dtos.responses.GetProjectSuggestionsResponse;
import application.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import jakarta.servlet.http.HttpServletRequest;
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
public class SuggestionsController {

    private final JwtService jwtService;
    private final SuggestionService suggestionService;

    @GetMapping("/project/{projectId}/suggestions")
    public GetProjectSuggestionsResponse getProjectSuggestions(
            @PathVariable("projectId") UUID projectId,
            @RequestParam(required = false) String status) throws AuthException {

        UUID userUuid = jwtService.getCurrentUserId();

        List<SuggestionDetailDto> suggestions = suggestionService.getSuggestionDetails(projectId, userUuid, status);

        log.info("User {} retrieved {} suggestions for project {} (status filter: {})",
                userUuid, suggestions.size(), projectId, status != null ? status : "all");

        return new GetProjectSuggestionsResponse(suggestions);
    }

    @GetMapping("/suggestions/{suggestionId}")
    public SuggestionDetailDto getSuggestion(
            @PathVariable("suggestionId") UUID suggestionId) throws AuthException {

        UUID userUuid = jwtService.getCurrentUserId();

        return suggestionService.getSuggestionDetail(suggestionId, userUuid);
    }

    @PostMapping("/suggestions/{suggestionId}/likes")
    @ResponseStatus(HttpStatus.CREATED)
    public String addLike(
            @PathVariable("suggestionId") UUID suggestionId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        suggestionService.addLike(suggestionId, userId);

        log.debug("User {} added like to suggestion {}", userId, suggestionId);
        return "Реакция добавлена";
    }

    @DeleteMapping("/suggestions/{suggestionId}/likes")
    public String removeLike(
            @PathVariable("suggestionId") UUID suggestionId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        suggestionService.removeLike(suggestionId, userId);

        log.debug("User {} removed like from suggestion {}", userId, suggestionId);
        return "Реакция удалена";
    }

    @PostMapping("/project/{projectId}/suggestions")
    @ResponseStatus(HttpStatus.CREATED)
    public SuggestionDetailDto createSuggestion(
            @PathVariable("projectId") UUID projectId,
            @RequestBody CreateAndUpdateSuggestionRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        SuggestionDetailDto dto = suggestionService.createSuggestion(projectId, request, userId);

        log.info("User {} created suggestion in project {}", userId, projectId);
        return dto;
    }

    @PutMapping("/suggestions/{suggestionId}")
    public SuggestionDetailDto updateSuggestion(
            @PathVariable("suggestionId") UUID suggestionId,
            @RequestBody CreateAndUpdateSuggestionRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        SuggestionDetailDto dto = suggestionService.updateSuggestion(suggestionId, request, userId);

        log.debug("User {} updated suggestion {}", userId, suggestionId);
        return dto;
    }

    @DeleteMapping("/suggestions/{suggestionId}")
    public String deleteSuggestion(
            @PathVariable("suggestionId") UUID suggestionId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        suggestionService.deleteSuggestion(suggestionId, userId);

        log.debug("User {} deleted suggestion {}", userId, suggestionId);
        return "Предложение или черновик успешно удалено";
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
