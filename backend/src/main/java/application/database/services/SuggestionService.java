package application.database.services;

import application.database.entities.Suggestion;
import application.database.repositories.LikeRepository;
import application.database.repositories.SuggestionRepository;
import application.dtos.SuggestionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final LikeRepository likeRepository;

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
}
