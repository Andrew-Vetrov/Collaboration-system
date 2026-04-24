package application.database.services;

import application.database.entities.ProjectRole;
import application.database.entities.UserRole;
import application.database.repositories.ProjectRoleRepository;
import application.database.repositories.UserRoleRepository;
import application.dtos.RoleDto;
import application.dtos.requests.CreateRoleRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final ProjectRoleRepository projectRoleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProjectService projectService;

    public ProjectRole createRole(UUID projectId, CreateRoleRequest request, UUID userId) {
        if (!projectService.isUserProjectAdmin(userId, projectId)) {
            throw new AccessDeniedException("User " + userId + " is not an admin of project: " + projectId);
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is not specified");
        }
        if (request.getColor() == null || request.getColor().isBlank()) {
            throw new IllegalArgumentException("Color is not specified");
        }

        if (projectRoleRepository.existsByProjectIdAndName(projectId, request.getName())) {
            throw new IllegalArgumentException("Role with such name already exists in project");
        }

        int likesAmount = (request.getLikesAmount() != null) ? request.getLikesAmount() : 0;
        if (likesAmount < 0) {
            throw new IllegalArgumentException("Likes amount cannot be negative");
        }

        ProjectRole role = ProjectRole.builder()
                .projectId(projectId)
                .name(request.getName())
                .color(request.getColor())
                .likesAmount(likesAmount)
                .build();

        return projectRoleRepository.save(role);
    }

    public List<RoleDto> getProjectRoles(UUID projectId, UUID currentUserId) {
        projectService.validateAndGetUserProjectAccess(currentUserId, projectId);

        List<ProjectRole> roles = projectRoleRepository.findAllByProjectId(projectId);
        return roles.stream()
                .map(RoleDto::new)
                .toList();
    }

    @Transactional
    public void deleteRole(UUID projectId, UUID roleId, UUID currentUserId) {
        if (!projectService.isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }

        ProjectRole role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

        if (!role.getProjectId().equals(projectId)) {
            throw new EntityNotFoundException("Role " + roleId + " does not belong to project " + projectId);
        }

        long assignedCount = userRoleRepository.countByRoleId(roleId);
        if (assignedCount > 0) {
            throw new IllegalArgumentException("Cannot delete role assigned to users");
        }

        projectRoleRepository.delete(role);
    }

    @Transactional
    public void assignRoleToUser(UUID projectId, UUID targetUserId, UUID roleId, UUID currentUserId) {
        if (!projectService.isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }

        ProjectRole role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

        if (!role.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("Role does not belong to project");
        }

        if (userRoleRepository.existsByUserIdAndRoleId(targetUserId, roleId)) {
            throw new IllegalArgumentException("User already has this role");
        }

        UserRole userRole = UserRole.builder()
                .userId(targetUserId)
                .projectId(projectId)
                .roleId(roleId)
                .build();

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void removeRoleFromUser(UUID projectId, UUID targetUserId, UUID roleId, UUID currentUserId) {
        if (!projectService.isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }

        UserRole userRole = userRoleRepository.findByUserIdAndRoleId(targetUserId, roleId)
                .orElseThrow(() -> new IllegalArgumentException("User does not have this role"));

        if (!userRole.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("Role does not belong to project");
        }

        userRoleRepository.delete(userRole);
    }

    @Transactional
    public ProjectRole updateRoleLikes(UUID projectId, UUID roleId, Integer likesAmount, UUID currentUserId) {
        if (!projectService.isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }

        if (likesAmount == null || likesAmount < 0) {
            throw new IllegalArgumentException("Invalid likes_amount value");
        }

        ProjectRole role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

        if (!role.getProjectId().equals(projectId)) {
            throw new EntityNotFoundException("Role " + roleId + " does not belong to project " + projectId);
        }

        role.setLikesAmount(likesAmount);
        return projectRoleRepository.save(role);
    }
}
