package application.database.services;

import application.database.entities.ProjectTag;
import application.database.entities.Suggestion;
import application.database.entities.SuggestionTag;
import application.database.repositories.ProjectTagRepository;
import application.database.repositories.SuggestionRepository;
import application.database.repositories.SuggestionTagRepository;
import application.dtos.ProjectTagDto;
import application.dtos.requests.SetTagRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TagService {

    private final ProjectTagRepository projectTagRepository;
    private final SuggestionRepository suggestionRepository;
    private final SuggestionTagRepository suggestionTagRepository;
    private final ProjectAccessService projectAccessService;

    public List<ProjectTagDto> getProjectTags(UUID projectId, UUID currentUserId) {
        projectAccessService.validateAndGetUserProjectAccess(currentUserId, projectId);

        List<ProjectTag> tags = projectTagRepository.findAllByProjectId(projectId);
        return tags.stream()
                .map(this::makeProjectTagDto)
                .toList();
    }

    @Transactional
    public ProjectTagDto createTag(UUID projectId, SetTagRequest request, UUID userId) {
        if (!projectAccessService.isUserProjectAdmin(userId, projectId)) {
            throw new AccessDeniedException("User " + userId + " is not an admin of project: " + projectId);
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is not specified");
        }

        if (projectTagRepository.existsByProjectIdAndName(projectId, request.getName())) {
            throw new IllegalArgumentException("Tag with such name already exists in project");
        }

        ProjectTag tag = ProjectTag.builder()
                .projectId(projectId)
                .name(request.getName())
                .color(request.getColor())
                .build();

        return makeProjectTagDto(projectTagRepository.save(tag));
    }

    @Transactional
    public ProjectTagDto updateTag(UUID projectId, UUID tagId, SetTagRequest request, UUID userId) {
        if (!projectAccessService.isUserProjectAdmin(userId, projectId)) {
            throw new AccessDeniedException("User " + userId + " is not an admin of project: " + projectId);
        }

        ProjectTag tag = projectTagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + tagId));

        if (!tag.getProjectId().equals(projectId)) {
            throw new EntityNotFoundException("Tag " + tagId + " does not belong to project " + projectId);
        }

        if (request.getName() != null && !request.getName().isBlank()) {
            if (!tag.getName().equals(request.getName()) && projectTagRepository.existsByProjectIdAndName(projectId, request.getName())) {
                throw new IllegalArgumentException("Tag with such name already exists in project");
            }
            tag.setName(request.getName());
        }

        if (request.getColor() != null) {
            tag.setColor(request.getColor());
        }

        return makeProjectTagDto(projectTagRepository.save(tag));
    }

    @Transactional
    public void deleteTag(UUID projectId, UUID tagId, UUID userId) {
        if (!projectAccessService.isUserProjectAdmin(userId, projectId)) {
            throw new AccessDeniedException("User " + userId + " is not an admin of project: " + projectId);
        }

        ProjectTag tag = projectTagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + tagId));

        if (!tag.getProjectId().equals(projectId)) {
            throw new EntityNotFoundException("Tag " + tagId + " does not belong to project " + projectId);
        }

        if (suggestionTagRepository.existsByIdTagId(tagId)) {
            throw new IllegalStateException("Tag cannot be deleted while it is used in suggestions");
        }

        projectTagRepository.delete(tag);
    }

    @Transactional
    public void attachTagToSuggestion(UUID suggestionId, UUID tagId, UUID userId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        projectAccessService.validateAndGetUserProjectAccess(userId, suggestion.getProjectId());

        ProjectTag tag = projectTagRepository.findById(tagId)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found: " + tagId));

        if (!tag.getProjectId().equals(suggestion.getProjectId())) {
            throw new IllegalArgumentException("Tag does not belong to the same project as suggestion");
        }

        SuggestionTag.SuggestionTagId compositionId = new SuggestionTag.SuggestionTagId(suggestionId, tagId);
        if (suggestionTagRepository.existsById(compositionId)) {
            throw new IllegalArgumentException("Tag is already attached to this suggestion");
        }

        SuggestionTag suggestionTag = SuggestionTag.builder()
                .id(compositionId)
                .build();

        suggestionTagRepository.save(suggestionTag);
    }

    @Transactional
    public void detachTagFromSuggestion(UUID suggestionId, UUID tagId, UUID userId) {
        Suggestion suggestion = suggestionRepository.findById(suggestionId)
                .orElseThrow(() -> new EntityNotFoundException("Suggestion not found: " + suggestionId));

        projectAccessService.validateAndGetUserProjectAccess(userId, suggestion.getProjectId());

        SuggestionTag.SuggestionTagId compositionId = new SuggestionTag.SuggestionTagId(suggestionId, tagId);
        if (!suggestionTagRepository.existsById(compositionId)) {
            throw new EntityNotFoundException("Tag is not attached to this suggestion");
        }

        suggestionTagRepository.deleteByIdSuggestionIdAndIdTagId(suggestionId, tagId);
    }

    private ProjectTagDto makeProjectTagDto(ProjectTag tag) {
        return new ProjectTagDto(
                tag.getId(),
                tag.getProjectId(),
                tag.getName(),
                tag.getColor()
        );
    }
}