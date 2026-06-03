package application.api.tags;

import application.database.services.TagService;
import application.dtos.ProjectTagDto;
import application.dtos.requests.AttachTagRequest;
import application.dtos.requests.SetTagRequest;
import application.dtos.responses.BasicSuccessResponse;
import application.dtos.responses.GetProjectTagsResponse;
import application.security.JwtService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TagsController {

    private final JwtService jwtService;
    private final TagService tagService;

    @GetMapping("/projects/{projectId}/tags")
    public GetProjectTagsResponse getProjectTags(
            @PathVariable("projectId") UUID projectId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        List<ProjectTagDto> tags = tagService.getProjectTags(projectId, userId);

        return new GetProjectTagsResponse(tags);
    }

    @PostMapping("/projects/{projectId}/tags")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectTagDto createTag(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody SetTagRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        return tagService.createTag(projectId, request, userId);
    }

    @PutMapping("/projects/{projectId}/tags/{tagId}")
    public ProjectTagDto updateTag(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("tagId") UUID tagId,
            @Valid @RequestBody SetTagRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        return tagService.updateTag(projectId, tagId, request, userId);
    }

    @DeleteMapping("/projects/{projectId}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BasicSuccessResponse deleteTag(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("tagId") UUID tagId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        tagService.deleteTag(projectId, tagId, userId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString();
        return new BasicSuccessResponse(HttpStatus.NO_CONTENT.value(), currentUri);
    }

    @PostMapping("/suggestions/{suggestionId}/tags")
    public BasicSuccessResponse attachTagToSuggestion(
            @PathVariable("suggestionId") UUID suggestionId,
            @Valid @RequestBody AttachTagRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        tagService.attachTagToSuggestion(suggestionId, request.getTagId(), userId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString();
        return new BasicSuccessResponse(HttpStatus.OK.value(), currentUri);
    }

    @DeleteMapping("/suggestions/{suggestionId}/tags/{tagId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BasicSuccessResponse detachTagFromSuggestion(
            @PathVariable("suggestionId") UUID suggestionId,
            @PathVariable("tagId") UUID tagId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        tagService.detachTagFromSuggestion(suggestionId, tagId, userId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString();
        return new BasicSuccessResponse(HttpStatus.NO_CONTENT.value(), currentUri);
    }
}