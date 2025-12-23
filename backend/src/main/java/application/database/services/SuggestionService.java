package application.database.services;

import application.database.entities.Project;
import application.database.entities.Suggestion;
import application.database.repositories.LikeRepository;
import application.database.repositories.SuggestionRepository;
import application.dtos.SuggestionDetailDto;
import application.dtos.SuggestionDto;
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

    public List<Suggestion> getSuggestions(UUID projectId, String statusStr) {
        if (statusStr == null || statusStr.isBlank()) {
            return suggestionRepository.findAllByProjectId(projectId);
        }
        return suggestionRepository.findAllByProjectIdAndStatus(projectId, statusStr);
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
                suggestion.getStatus()
        );
    }

    public SuggestionDetailDto getSuggestionDetail(UUID suggestionId, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new IllegalArgumentException("Suggestion not found: " + suggestionId));

        // Проверка доступа: пользователь должен быть в проекте предложения
        List<Project> userProjects = projectService.getUserProjects(currentUserId);
        boolean hasAccess = userProjects.stream()
                .anyMatch(p -> p.getId().equals(suggestion.getProjectId()));

        if (!hasAccess) {
            throw new AccessDeniedException("User has no access to suggestion: " + suggestionId);
        }

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
                suggestion.getStatus()
        );
    }
}
