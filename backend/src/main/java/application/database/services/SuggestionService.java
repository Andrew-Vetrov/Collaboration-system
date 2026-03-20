package application.database.services;

import application.database.entities.Like;
import application.database.entities.Project;
import application.database.entities.ProjectRights;
import application.database.entities.Suggestion;
import application.database.repositories.LikeRepository;
import application.database.repositories.SuggestionRepository;
import application.dtos.SuggestionDetailDto;
import application.dtos.requests.CreateAndUpdateSuggestionRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final LikeRepository likeRepository;
    private final ProjectService projectService;

    public List<SuggestionDetailDto> getSuggestionDetails(UUID projectId, UUID currentUserId, String statusStr) {
        projectService.getProjectById(projectId); // Проверка существования проекта
        projectService.getUserProjectRights(currentUserId, projectId) // Проверка прав
                .orElseThrow(() -> new AccessDeniedException("User " + currentUserId + " has no rights to project " + projectId));
        List<Suggestion> suggestions;
        if (statusStr == null || statusStr.isBlank()) {
            suggestions = suggestionRepository.findAllByProjectId(projectId);
        }
        else {
            suggestions = suggestionRepository
                    .findAllByProjectIdAndStatus(projectId, Suggestion.SuggestionStatus.valueOf(statusStr.toUpperCase()));
        }

        List<SuggestionDetailDto> dtos = new ArrayList<>();
        for(Suggestion suggestion : suggestions){
            dtos.add(makeSuggestionDetailDto(suggestion, currentUserId));
        }

        return dtos;
    }

    public SuggestionDetailDto getSuggestionDetail(UUID suggestionId, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        // Проверка доступа: пользователь должен быть в проекте предложения
        projectService.getUserProjectRights(currentUserId, suggestion.getProjectId())
                .orElseThrow(() -> new AccessDeniedException("User " + currentUserId + " has no rights to suggestion " + suggestionId));

        return makeSuggestionDetailDto(suggestion, currentUserId);
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
        UUID projectId = suggestion.getProjectId();
        projectService.validateAndGetUserProjectAccess(userId, projectId);
        Project project = projectService.getProjectById(projectId);

        ZonedDateTime currentPeriodStart = project.getVotePeriodStart();

        // Ищем все лайки этого пользователя на это предложение
        List<Like> userLikes = likeRepository.findByUserIdAndSuggestionId(userId, suggestionId);
        if (userLikes.isEmpty()) {
            throw new EntityNotFoundException("No like found for user " + userId + " on suggestion " + suggestionId);
        }

        // Фильтруем только лайки из текущего периода
        List<Like> removableLikes = userLikes.stream()
                .filter(like -> !like.getPlacedAt().isBefore(currentPeriodStart))
                .toList();

        if (removableLikes.isEmpty()) {
            throw new IllegalStateException(
                    "No likes from the current voting period. You can only remove likes placed after " +
                            currentPeriodStart);
        }

        likeRepository.delete(removableLikes.getFirst());
        projectService.restoreVote(userId, projectId);
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

        return makeSuggestionDetailDto(saved, currentUserId);
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

        return makeSuggestionDetailDto(suggestion, currentUserId);
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

    private SuggestionDetailDto makeSuggestionDetailDto(Suggestion suggestion, UUID userId){
        long likesAmount = likeRepository.countBySuggestionId(suggestion.getId());
        long userLikesAmount = likeRepository.countByUserIdAndSuggestionId(userId, suggestion.getId());
        return new SuggestionDetailDto(
                suggestion.getId(),
                suggestion.getUserId(),
                suggestion.getProjectId(),
                suggestion.getPlacedAt(),
                suggestion.getLastEdit(),
                likesAmount,
                userLikesAmount,
                suggestion.getName(),
                suggestion.getDescription(),
                suggestion.getStatus().toString().toLowerCase()
        );
    }
}
