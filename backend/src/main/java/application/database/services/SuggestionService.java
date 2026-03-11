package application.database.services;

import application.database.entities.Project;
import application.database.entities.Suggestion;
import application.database.repositories.LikeRepository;
import application.database.repositories.SuggestionRepository;
import application.dtos.SuggestionDetailDto;
import application.dtos.SuggestionDto;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final LikeRepository likeRepository;
    private final ProjectService projectService;

    public List<Suggestion> getSuggestions(UUID projectId, UUID currentUserId, String statusStr) {
        projectService.getProjectById(projectId); // Проверка существования проекта
        projectService.getUserProjectRights(currentUserId, projectId) // Проверка прав
                .orElseThrow(() -> new AccessDeniedException("User " + currentUserId + " has no rights to project " + projectId));
        if (statusStr == null || statusStr.isBlank()) {
            return suggestionRepository.findAllByProjectId(projectId);
        }
        return suggestionRepository.findAllByProjectIdAndStatus(projectId, Suggestion.SuggestionStatus.valueOf(statusStr));
    }

    public SuggestionDto convertToSuggestionDtoWithLikes(Suggestion suggestion) {
        long likesCount = likeRepository.countBySuggestionId(suggestion.getId());

        return new SuggestionDto(
                suggestion.getId(),
                suggestion.getUserId(),
                suggestion.getProjectId(),
                suggestion.getPlacedAt(),
                suggestion.getLastEdit(),
                likesCount,
                suggestion.getName(),
                suggestion.getDescription(),
                suggestion.getStatus().toString()
        );
    }

    public SuggestionDetailDto getSuggestionDetail(UUID suggestionId, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        // Проверка доступа: пользователь должен быть в проекте предложения
        projectService.getUserProjectRights(currentUserId, suggestion.getProjectId())
                .orElseThrow(() -> new AccessDeniedException("User " + currentUserId + " has no rights to suggestion " + suggestionId));

        long likesAmount = likeRepository.countBySuggestionId(suggestionId);

        return new SuggestionDetailDto(
                suggestion.getId(),
                suggestion.getUserId(),
                suggestion.getProjectId(),
                suggestion.getPlacedAt(),
                suggestion.getLastEdit(),
                likesAmount,
                suggestion.getName(),
                suggestion.getDescription(),
                suggestion.getStatus().toString()
        );
    }
}
