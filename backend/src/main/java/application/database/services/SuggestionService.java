package application.database.services;

import application.database.entities.Like;
import application.database.entities.Project;
import application.database.entities.Suggestion;
import application.database.repositories.LikeRepository;
import application.database.repositories.SuggestionRepository;
import application.dtos.SuggestionDetailDto;
import application.dtos.SuggestionDto;
import application.dtos.requests.CreateAndUpdateSuggestionRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Comparator;
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
        return suggestionRepository.findAllByProjectIdAndStatus(projectId, Suggestion.SuggestionStatus.valueOf(statusStr.toUpperCase()));
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
                suggestion.getStatus().toString().toLowerCase()
        );
    }

    public SuggestionDetailDto getSuggestionDetail(UUID suggestionId, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        // Проверка доступа: пользователь должен быть в проекте предложения
        projectService.getUserProjectRights(currentUserId, suggestion.getProjectId())
                .orElseThrow(() -> new AccessDeniedException("User " + currentUserId + " has no rights to suggestion " + suggestionId));

        long likesAmount = likeRepository.countBySuggestionId(suggestionId);

        return makeSuggestionDetailDto(suggestion, likesAmount);
    }

    @Transactional
    public void addLike(UUID suggestionId, UUID userId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        projectService.consumeVote(userId, suggestion.getProjectId());

        Like like = Like.builder()
                .userId(userId)
                .suggestionId(suggestionId)
                .placedAt(ZonedDateTime.now())
                .build();

        likeRepository.save(like);
    }

    @Transactional
    public void removeLike(UUID suggestionId, UUID userId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        // restoreVote внутри проверит доступ
        List<Like> userLikes = likeRepository.findByUserIdAndSuggestionId(userId, suggestionId);
        if (userLikes.isEmpty()) {
            throw new EntityNotFoundException("No like found for user " + userId + " on suggestion " + suggestionId);
        }

        Like oldestLike = userLikes.stream()
                .min(Comparator.comparing(Like::getPlacedAt))
                .orElseThrow();

        likeRepository.delete(oldestLike);
        projectService.restoreVote(userId, suggestion.getProjectId());
    }

    @Transactional
    public SuggestionDetailDto createSuggestion(UUID projectId, CreateAndUpdateSuggestionRequest request, UUID currentUserId) {
        projectService.validateAndGetUserProjectAccess(currentUserId, projectId);

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }

        String statusStr = request.getStatus();
        Suggestion.SuggestionStatus status = (statusStr == null || statusStr.isBlank())
                ? Suggestion.SuggestionStatus.DRAFT
                : Suggestion.SuggestionStatus.valueOf(statusStr.toUpperCase());

        ZonedDateTime now = ZonedDateTime.now();

        Suggestion suggestion = Suggestion.builder()
                .userId(currentUserId)
                .projectId(projectId)
                .placedAt(now)
                .lastEdit(now)
                .name(request.getName())
                .description(request.getDescription())
                .status(status)
                .build();

        Suggestion saved = suggestionRepository.save(suggestion);

        long likesAmount = likeRepository.countBySuggestionId(saved.getId());

        return makeSuggestionDetailDto(saved, likesAmount);
    }

    @Transactional
    public SuggestionDetailDto updateSuggestion(UUID suggestionId, CreateAndUpdateSuggestionRequest request, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        projectService.validateAndGetUserProjectAccess(currentUserId, suggestion.getProjectId());

        if (!suggestion.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Only the author can update the suggestion");
        }

        ZonedDateTime now = ZonedDateTime.now();

        if (request.getName() != null) {
            suggestion.setName(request.getName());
        }
        if (request.getDescription() != null) {
            suggestion.setDescription(request.getDescription());
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            Suggestion.SuggestionStatus newStatus = Suggestion.SuggestionStatus.valueOf(request.getStatus().toUpperCase());
            if (suggestion.getStatus() == Suggestion.SuggestionStatus.DRAFT &&
                    newStatus != Suggestion.SuggestionStatus.DRAFT) {
                suggestion.setPlacedAt(now);
            }
            else if (suggestion.getStatus() != Suggestion.SuggestionStatus.DRAFT &&
                    newStatus == Suggestion.SuggestionStatus.DRAFT) {
                throw new IllegalArgumentException("Cannot change status of placed suggestion to DRAFT");
            }
            suggestion.setStatus(newStatus);
        }

        suggestion.setLastEdit(now);
        suggestionRepository.save(suggestion);

        long likesAmount = likeRepository.countBySuggestionId(suggestionId);

        return makeSuggestionDetailDto(suggestion, likesAmount);
    }

    @Transactional
    public void deleteSuggestion(UUID suggestionId, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        if (!projectService.isUserProjectAdmin(currentUserId, suggestion.getProjectId())) {
            throw new AccessDeniedException("Only project admins can delete suggestions");
        }

        suggestionRepository.delete(suggestion);
    }

    private SuggestionDetailDto makeSuggestionDetailDto(Suggestion suggestion, long likesAmount){
        return new SuggestionDetailDto(
                suggestion.getId(),
                suggestion.getUserId(),
                suggestion.getProjectId(),
                suggestion.getPlacedAt(),
                suggestion.getLastEdit(),
                likesAmount,
                suggestion.getName(),
                suggestion.getDescription(),
                suggestion.getStatus().toString().toLowerCase()
        );
    }
}
