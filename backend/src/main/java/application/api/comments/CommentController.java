package application.api.comments;

import application.dtos.requests.CommentRequestDto;
import application.dtos.responses.CommentResponseDto;
import application.database.services.CommentService;
import application.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import java.util.List;
import java.util.UUID;

@RestController
public class CommentController {

    private final CommentService commentService;
    private final JwtService jwtService;

    public CommentController(CommentService commentService, JwtService jwtService) {
        this.commentService = commentService;
        this.jwtService = jwtService;
    }

    @GetMapping("/suggestions/{suggestionId}/comments")
    public ResponseEntity<List<CommentResponseDto>> getComments(@PathVariable UUID suggestionId) throws AuthException {
        UUID currentUserId = jwtService.getCurrentUserId();

        try {
            commentService.checkAccessToSuggestion(currentUserId, suggestionId);

            List<CommentResponseDto> comments = commentService.getCommentsBySuggestionId(suggestionId, currentUserId);
            return ResponseEntity.ok(comments);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @PostMapping("/suggestions/{suggestionId}/comments")
    public ResponseEntity<CommentResponseDto> addComment(
            @PathVariable UUID suggestionId,
            @RequestBody CommentRequestDto requestDto
    ) throws AuthException {
        UUID currentUserId = jwtService.getCurrentUserId();

        try {
            commentService.checkAccessToSuggestion(currentUserId, suggestionId);

            CommentResponseDto createdComment = commentService.addComment(suggestionId, currentUserId, requestDto);
            return ResponseEntity.status(201).body(createdComment);

        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @PostMapping("/comments/{commentId}/reply")
    public ResponseEntity<CommentResponseDto> replyToComment(
            @PathVariable UUID commentId,
            @RequestBody CommentRequestDto requestDto
    ) throws AuthException {
        UUID currentUserId = jwtService.getCurrentUserId();

        try {

            CommentResponseDto reply = commentService.addReplyToComment(commentId, currentUserId, requestDto);
            return ResponseEntity.status(201).body(reply);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable UUID commentId) throws AuthException {
        UUID currentUserId = jwtService.getCurrentUserId();

        try {
            commentService.deleteCommentById(commentId, currentUserId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}