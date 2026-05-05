package application.api.roles;

import application.database.entities.ProjectRole;
import application.database.services.RoleService;
import application.dtos.RoleDto;
import application.dtos.requests.AssignRoleRequest;
import application.dtos.requests.SetRoleRequest;
import application.dtos.responses.BasicSuccessResponse;
import application.dtos.responses.GetProjectRolesResponse;
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
public class RolesController {

    private final JwtService jwtService;
    private final RoleService roleService;

    @PostMapping("/projects/{projectId}/roles")
    @ResponseStatus(HttpStatus.CREATED)
    public RoleDto createRole(
            @PathVariable("projectId") UUID projectId,
            @Valid @RequestBody SetRoleRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        RoleDto createdRole = roleService.createRole(projectId, request, userId);

        return createdRole;
    }

    @GetMapping("/projects/{projectId}/roles")
    public GetProjectRolesResponse getProjectRoles(
            @PathVariable("projectId") UUID projectId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        List<RoleDto> roles = roleService.getProjectRoles(projectId, userId);

        return new GetProjectRolesResponse(roles);
    }

    @PutMapping("/projects/{projectId}/roles/{roleId}")
    public RoleDto updateRole(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("roleId") UUID roleId,
            @Valid @RequestBody SetRoleRequest request) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        RoleDto updatedRole = roleService.updateRole(projectId, roleId, request, userId);

        return updatedRole;
    }

    @DeleteMapping("/projects/{projectId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BasicSuccessResponse deleteRole(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("roleId") UUID roleId) throws AuthException {

        UUID userId = jwtService.getCurrentUserId();
        roleService.deleteRole(projectId, roleId, userId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString();
        return new BasicSuccessResponse(HttpStatus.NO_CONTENT.value(), currentUri);
    }

    @PostMapping("/projects/{projectId}/users/{userId}/roles")
    public BasicSuccessResponse assignRoleToUser(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId,
            @Valid @RequestBody AssignRoleRequest request) throws AuthException {

        UUID currentUserId = jwtService.getCurrentUserId();
        roleService.assignRoleToUser(projectId, userId, request.getRoleId(), currentUserId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString();
        return new BasicSuccessResponse(HttpStatus.OK.value(), currentUri);
    }

    @DeleteMapping("/projects/{projectId}/users/{userId}/roles/{roleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BasicSuccessResponse removeRoleFromUser(
            @PathVariable("projectId") UUID projectId,
            @PathVariable("userId") UUID userId,
            @PathVariable("roleId") UUID roleId) throws AuthException {

        UUID currentUserId = jwtService.getCurrentUserId();
        roleService.removeRoleFromUser(projectId, userId, roleId, currentUserId);

        String currentUri = ServletUriComponentsBuilder.fromCurrentRequestUri().build().toUriString();
        return new BasicSuccessResponse(HttpStatus.NO_CONTENT.value(), currentUri);
    }
}