package application.api.roles;

import application.database.entities.ProjectRole;
import application.database.services.RoleService;
import application.dtos.RoleDto;
import application.dtos.requests.AssignRoleRequest;
import application.dtos.requests.CreateRoleRequest;
import application.dtos.requests.UpdateRoleLikesRequest;
import application.dtos.responses.GetProjectRolesResponse;
import application.security.JwtService;
import application.database.services.ProjectService;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RolesController {

    private final JwtService jwtService;
    private final RoleService roleService;

    @PostMapping("/projects/{projectId}/roles")
    public RoleDto createRole(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody CreateRoleRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        ProjectRole createdRole = roleService.createRole(projectId, request, userId);

        return new RoleDto(createdRole);
    }

    @GetMapping("/projects/{projectId}/roles")
    public GetProjectRolesResponse getProjectRoles(
            @PathVariable("projectId") UUID projectId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        List<RoleDto> roles = roleService.getProjectRoles(projectId, userId);

        return new GetProjectRolesResponse(roles);
    }

    @DeleteMapping("/projects/{projectId}/roles/{roleId}")
    public String deleteRole(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("roleId") UUID roleId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        roleService.deleteRole(projectId, roleId, userId);

        return "Роль успешно удалена";
    }

    @PostMapping("/projects/{projectId}/users/{userId}/roles")
    public String assignRoleToUser(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody AssignRoleRequest request) throws AuthException {

        UUID currentUserId = jwtService.getCurrentUserId();
        roleService.assignRoleToUser(projectId, userId, request.getRoleId(), currentUserId);

        return "Роль успешно добавлена пользователю";
    }

    @DeleteMapping("/projects/{projectId}/users/{userId}/roles/{roleId}")
    public String removeRoleFromUser(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId,
            @PathVariable("roleId") UUID roleId) throws AuthException {

        UUID currentUserId = jwtService.getCurrentUserId();
        roleService.removeRoleFromUser(projectId, userId, roleId, currentUserId);

        return "Роль успешно удалена у пользователя";
    }

    @PutMapping("/projects/{projectId}/roles/{roleId}/likes")
    public RoleDto updateRoleLikes(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("roleId") UUID roleId,
            @Valid @RequestBody UpdateRoleLikesRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        ProjectRole updatedRole = roleService.updateRoleLikes(projectId, roleId, request.getLikesAmount(), userId);

        return new RoleDto(updatedRole);
    }
}