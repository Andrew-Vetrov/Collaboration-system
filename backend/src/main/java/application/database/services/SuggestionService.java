package application.database.services;

import application.database.entities.Like;
import application.database.entities.Project;
import application.database.entities.Suggestion;
import application.database.entities.SuggestionTag;
import application.database.repositories.LikeRepository;
import application.database.repositories.SuggestionRepository;
import application.database.repositories.SuggestionTagRepository;
import application.dtos.SuggestionDetailDto;
import application.dtos.TagDto;
import application.dtos.requests.CreateAndUpdateSuggestionRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SuggestionService {
    private final SuggestionRepository suggestionRepository;
    private final LikeRepository likeRepository;
    private final ProjectService projectService;
    private final ProjectAccessService projectAccessService;
    private final SuggestionTagRepository suggestionTagRepository;

    public List<SuggestionDetailDto> getSuggestionDetails(
            UUID projectId,
            UUID currentUserId,
            String statusStr,
            List<UUID> tagIds
    ) {
        projectService.getProjectById(projectId); // Проверка существования проекта
        projectAccessService.getUserProjectRights(currentUserId, projectId) // Проверка прав
                .orElseThrow(() -> new AccessDeniedException("User " + currentUserId + " has no rights to project " + projectId));

        List<Suggestion> suggestions;

        if (statusStr == null || statusStr.isBlank()) {
            List<Suggestion> tempSuggestions = suggestionRepository.findAllByProjectId(projectId);
            suggestions = new ArrayList<>();
            for (Suggestion suggestion : tempSuggestions){
                if(suggestion.getStatus() != Suggestion.SuggestionStatus.DRAFT || suggestion.getUserId().equals(currentUserId)){
                    suggestions.add(suggestion);
                }
            }
        }
        else {
            Suggestion.SuggestionStatus status = Suggestion.SuggestionStatus.valueOf(statusStr.toUpperCase());
            if(status != Suggestion.SuggestionStatus.DRAFT) {
                suggestions = suggestionRepository
                        .findAllByProjectIdAndStatus(projectId, status);
            }
            else{
                suggestions = suggestionRepository
                        .findAllByProjectIdAndUserIdAndStatus(projectId, currentUserId, status);
            }
        }

        if (tagIds != null && !tagIds.isEmpty()) {
            suggestions = suggestions.stream()
                    .filter(suggestion -> {
                        List<UUID> suggestionTagIds = suggestionTagRepository.findTagIdsBySuggestionId(suggestion.getId());
                        return new HashSet<>(suggestionTagIds).containsAll(tagIds);
                    })
                    .toList();
        }

        List<SuggestionDetailDto> dtos = new ArrayList<>();
        for(Suggestion suggestion : suggestions){
            List<TagDto> tagDtos = suggestionTagRepository.findAllTagsBySuggestionId(suggestion.getId())
                    .stream()
                    .map(tag -> new TagDto(tag.getId(), tag.getName(), tag.getColor()))
                    .toList();

            dtos.add(makeSuggestionDetailDto(suggestion, currentUserId, tagDtos));
        }

        return dtos;
    }

    public SuggestionDetailDto getSuggestionDetail(UUID suggestionId, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        projectAccessService.getUserProjectRights(currentUserId, suggestion.getProjectId())
                .orElseThrow(() -> new AccessDeniedException("User " + currentUserId + " has no rights to suggestion " + suggestionId));

        if (suggestion.getStatus() == Suggestion.SuggestionStatus.DRAFT
                && !suggestion.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("Suggestion " + currentUserId + " is another user's draft");
        }

        List<TagDto> tagDtos = suggestionTagRepository.findAllTagsBySuggestionId(suggestionId)
                .stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName(), tag.getColor()))
                .toList();

        return makeSuggestionDetailDto(suggestion, currentUserId, tagDtos);
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
        projectAccessService.validateAndGetUserProjectAccess(userId, projectId);
        Project project = projectService.getProjectById(projectId);

        ZonedDateTime currentPeriodStart = project.getVotePeriodStart();

        List<Like> userLikes = likeRepository.findByUserIdAndSuggestionId(userId, suggestionId);
        if (userLikes.isEmpty()) {
            throw new EntityNotFoundException("No like found for user " + userId + " on suggestion " + suggestionId);
        }

        List<Like> removableLikes = userLikes.stream()
                .filter(like -> !like.getPlacedAt().isBefore(currentPeriodStart))
                .toList();

        if (removableLikes.isEmpty()) {
            throw new EntityNotFoundException(
                    "No likes from the current voting period. You can only remove likes placed after " +
                            currentPeriodStart);
        }

        likeRepository.delete(removableLikes.getFirst());
        projectService.restoreVote(userId, projectId);
    }

    @Transactional
    public SuggestionDetailDto createSuggestion(UUID projectId, CreateAndUpdateSuggestionRequest request, UUID currentUserId) {
        projectAccessService.validateAndGetUserProjectAccess(currentUserId, projectId);

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

        if (request.getTagIds() != null && !request.getTagIds().isEmpty()) {
            List<SuggestionTag> mappings = request.getTagIds().stream()
                    .map(tagId -> new SuggestionTag(new SuggestionTag.SuggestionTagId(saved.getId(), tagId)))
                    .toList();
            suggestionTagRepository.saveAll(mappings);
        }

        List<TagDto> tagDtos = suggestionTagRepository.findAllTagsBySuggestionId(saved.getId())
                .stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName(), tag.getColor()))
                .toList();

        return makeSuggestionDetailDto(saved, currentUserId, tagDtos);
    }

    @Transactional
    public SuggestionDetailDto updateSuggestion(UUID suggestionId, CreateAndUpdateSuggestionRequest request, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        var userRights = projectAccessService.validateAndGetUserProjectAccess(currentUserId, suggestion.getProjectId());

        if (!suggestion.getUserId().equals(currentUserId) && !userRights.getIsAdmin()) {
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

        if (request.getTagIds() != null) {
            suggestionTagRepository.deleteAllByIdSuggestionId(suggestionId);

            List<SuggestionTag> newMappings = request.getTagIds().stream()
                    .map(tagId -> new SuggestionTag(new SuggestionTag.SuggestionTagId(suggestionId, tagId)))
                    .toList();
            suggestionTagRepository.saveAll(newMappings);
        }

        List<TagDto> tagDtos = suggestionTagRepository.findAllTagsBySuggestionId(suggestionId)
                .stream()
                .map(tag -> new TagDto(tag.getId(), tag.getName(), tag.getColor()))
                .toList();

        return makeSuggestionDetailDto(suggestion, currentUserId, tagDtos);
    }

    @Transactional
    public void deleteSuggestion(UUID suggestionId, UUID currentUserId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        if (!projectAccessService.isUserProjectAdmin(currentUserId, suggestion.getProjectId())) {
            throw new AccessDeniedException("Only project admins can delete suggestions");
        }

        Project project = projectService.getProjectById(suggestion.getProjectId());
        List<Like> suggestionLikes = likeRepository.findBySuggestionId(suggestionId);
        List<Like> removableLikes = suggestionLikes.stream()
                .filter(like -> !like.getPlacedAt().isBefore(project.getVotePeriodStart()))
                .toList();
        for(Like like : removableLikes){
            likeRepository.delete(like);
            projectService.restoreVote(like.getUserId(), project.getId());
        }

        suggestionRepository.delete(suggestion);
    }

    private SuggestionDetailDto makeSuggestionDetailDto(
            Suggestion suggestion,
            UUID userId,
            List<TagDto> tags
    ) {
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
                suggestion.getStatus().toString().toLowerCase(),
                tags
        );
    }
}
