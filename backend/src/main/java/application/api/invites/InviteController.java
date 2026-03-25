package application.api.invites;

import application.database.services.ProjectService;
import application.database.services.UserService;
import application.dtos.requests.InviteRequestDto;
import application.dtos.responses.InviteResponseDto;
import application.database.services.InviteService;
import application.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.security.auth.message.AuthException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class InviteController {

    private final InviteService inviteService;
    private final JwtService jwtService;

    private final UserService userService;

    private final ProjectService projectService;

    @PostMapping("/projects/{project_id}/invites")
    public ResponseEntity<Void> inviteUser(
            @PathVariable("project_id") UUID projectId,
            @RequestParam("user_email") String userEmail) throws AuthException {
        try {
            inviteService.inviteUserToProject(projectId, userEmail, userService.findById(jwtService.getCurrentUserId()).getNickname());
            inviteService.sendInvite(userEmail, projectService.getProjectById(projectId).getName(), projectId.toString());
            return ResponseEntity.status(201).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @GetMapping("/projects/{project_id}/invites")
    public ResponseEntity<List<InviteResponseDto>> getProjectInvites(@PathVariable("project_id") UUID projectId) {
        try {
            List<InviteResponseDto> invites = inviteService.getProjectInvites(projectId);
            return ResponseEntity.ok(invites);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @GetMapping("/invites")
    public ResponseEntity<List<InviteResponseDto>> getUserInvites() throws AuthException {
        List<InviteResponseDto> invites = inviteService.getUserInvites(userService.findById(jwtService.getCurrentUserId()).getMail());
        return ResponseEntity.ok(invites);
    }

    @DeleteMapping("/invites/{invite_id}")
    public ResponseEntity<Void> handleInvite(
            @PathVariable("invite_id") UUID inviteId,
            @RequestParam("accept") boolean accept) throws AuthException {
        try {
            inviteService.handleInvite(inviteId, accept);
            return ResponseEntity.ok().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(404).build();
        }
    }
}
