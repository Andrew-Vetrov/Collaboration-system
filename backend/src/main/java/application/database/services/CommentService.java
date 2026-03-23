package application.database.services;

import application.dtos.requests.CommentRequestDto;
import application.dtos.responses.CommentResponseDto;
import application.database.entities.Comment;
import application.database.repositories.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final SuggestionService suggestionService;

    @Transactional
    public List<CommentResponseDto> getCommentsBySuggestionId(UUID suggestionId, UUID currentUserId) {
        suggestionService.getSuggestionDetail(suggestionId, currentUserId);

        return commentRepository.findBySuggestionId(suggestionId).stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponseDto addComment(UUID suggestionId, UUID authorId, CommentRequestDto requestDto) {
        suggestionService.getSuggestionDetail(suggestionId, authorId);

        Comment comment = new Comment();
        comment.setSuggestionId(suggestionId);
        comment.setUserId(authorId);
        comment.setText(requestDto.getText());

        Comment savedComment = commentRepository.save(comment);

        return mapToResponseDto(savedComment);
    }

    @Transactional
    public CommentResponseDto addReplyToComment(UUID parentCommentId, UUID currentUserId, CommentRequestDto requestDto) {
        Comment parentComment = commentRepository.findById(parentCommentId)
                .orElseThrow(() -> new EntityNotFoundException("The parent comment does not exist."));

        checkAccessToSuggestion(currentUserId, parentComment.getSuggestionId());

        Comment replyComment = new Comment();
        replyComment.setSuggestionId(parentComment.getSuggestionId());
        replyComment.setUserId(currentUserId);
        replyComment.setText(requestDto.getText());
        replyComment.setCommentReplyToId(parentComment.getId());

        Comment savedReply = commentRepository.save(replyComment);

        return mapToResponseDto(savedReply);
    }

    @Transactional
    public void deleteCommentById(UUID commentId, UUID currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found."));

        if (!comment.getUserId().equals(currentUserId)) {
            throw new AccessDeniedException("User " + currentUserId + " has no rights to delete a comment " + commentId);
        }

        commentRepository.delete(comment);
    }

    public void checkAccessToSuggestion(UUID currentUserId, UUID suggestionId) {
        suggestionService.getSuggestionDetail(suggestionId, currentUserId);
    }

    private CommentResponseDto mapToResponseDto(Comment comment) {
        return new CommentResponseDto(
                comment.getId(),
                comment.getUserId(),
                comment.getSuggestionId(),
                comment.getCommentReplyToId(),
                comment.getPlacedAt(),
                comment.getLastEdit(),
                comment.getText()
        );
    }
}