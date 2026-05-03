package application.database.services;

import application.database.entities.ProjectRights;
import application.database.entities.ProjectRole;
import application.database.entities.UserRole;
import application.database.repositories.ProjectRoleRepository;
import application.database.repositories.UserRoleRepository;
import application.dtos.RoleDto;
import application.dtos.requests.SetRoleRequest;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final ProjectRoleRepository projectRoleRepository;
    private final UserRoleRepository userRoleRepository;
    private final ProjectAccessService projectAccessService;

    public RoleDto createRole(UUID projectId, SetRoleRequest request, UUID userId) {
        if (!projectAccessService.isUserProjectAdmin(userId, projectId)) {
            throw new AccessDeniedException("User " + userId + " is not an admin of project: " + projectId);
        }

        validateSetRoleRequest(request);

        if (projectRoleRepository.existsByProjectIdAndName(projectId, request.getName())) {
            throw new IllegalArgumentException("Role with such name already exists in project");
        }

        ProjectRole role = ProjectRole.builder()
                .projectId(projectId)
                .name(request.getName())
                .color(request.getColor())
                .likesAmount(request.getLikesAmount())
                .build();

        return makeRoleDto(projectRoleRepository.save(role));
    }

    public List<RoleDto> getProjectRoles(UUID projectId, UUID currentUserId) {
        projectAccessService.validateAndGetUserProjectAccess(currentUserId, projectId);

        List<ProjectRole> roles = projectRoleRepository.findAllByProjectId(projectId);
        return roles.stream()
                .map(this::makeRoleDto)
                .toList();
    }

    @Transactional
    public void deleteRole(UUID projectId, UUID roleId, UUID currentUserId) {
        if (!projectAccessService.isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }

        ProjectRole role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

        if (!role.getProjectId().equals(projectId)) {
            throw new EntityNotFoundException("Role " + roleId + " does not belong to project " + projectId);
        }

        long assignedCount = userRoleRepository.countByProjectRole_Id(roleId);
        if (assignedCount > 0) {
            throw new IllegalArgumentException("Cannot delete role assigned to users");
        }

        projectRoleRepository.delete(role);
    }

    @Transactional
    public void assignRoleToUser(UUID projectId, UUID targetUserId, UUID roleId, UUID currentUserId) {
        if (!projectAccessService.isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }
        Optional<ProjectRights> userRights = projectAccessService.getUserProjectRights(targetUserId, projectId);
        if (userRights.isEmpty()) {
            throw new EntityNotFoundException("User " + targetUserId + " is not a member of project " + projectId);
        }


        ProjectRole role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

        if (!role.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("Role does not belong to project");
        }

        if (userRoleRepository.existsByUserIdAndProjectRole_Id(targetUserId, roleId)) {
            throw new IllegalArgumentException("User already has this role");
        }

        UserRole userRole = UserRole.builder()
                .userId(targetUserId)
                .projectId(projectId)
                .projectRole(role)
                .build();

        userRoleRepository.save(userRole);
    }

    @Transactional
    public void removeRoleFromUser(UUID projectId, UUID targetUserId, UUID roleId, UUID currentUserId) {
        if (!projectAccessService.isUserProjectAdmin(currentUserId, projectId)) {
            throw new AccessDeniedException("User " + currentUserId + " is not an admin of project: " + projectId);
        }
        Optional<ProjectRights> userRights = projectAccessService.getUserProjectRights(targetUserId, projectId);
        if (userRights.isEmpty()) {
            throw new EntityNotFoundException("User " + targetUserId + " is not a member of project " + projectId);
        }

        UserRole userRole = userRoleRepository.findByUserIdAndProjectRole_Id(targetUserId, roleId)
                .orElseThrow(() -> new IllegalArgumentException("User does not have this role"));

        if (!userRole.getProjectId().equals(projectId)) {
            throw new IllegalArgumentException("Role does not belong to project");
        }

        userRoleRepository.delete(userRole);
    }

    @Transactional
    public RoleDto updateRole(UUID projectId, UUID roleId, SetRoleRequest request, UUID userId) {
        if (!projectAccessService.isUserProjectAdmin(userId, projectId)) {
            throw new AccessDeniedException("User " + userId + " is not an admin of project: " + projectId);
        }
        validateSetRoleRequest(request);

        if (projectRoleRepository.existsByProjectIdAndName(projectId, request.getName())) {
            throw new IllegalArgumentException("Role with such name already exists in project");
        }

        ProjectRole role = projectRoleRepository.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + roleId));

        if (!role.getProjectId().equals(projectId)) {
            throw new EntityNotFoundException("Role " + roleId + " does not belong to project " + projectId);
        }

        role.setName(request.getName());
        role.setColor(request.getColor());
        role.setLikesAmount(request.getLikesAmount());
        return makeRoleDto(projectRoleRepository.save(role));
    }
    private void validateSetRoleRequest(SetRoleRequest request){
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Name is not specified");
        }
        if (request.getColor() == null || request.getColor().isBlank()) {
            throw new IllegalArgumentException("Color is not specified");
        }
        if(request.getLikesAmount() == null){
            throw new IllegalArgumentException("Likes amount is not specified");
        }
        if (request.getLikesAmount() < 0) {
            throw new IllegalArgumentException("Likes amount cannot be negative");
        }
    }

    public List<RoleDto> getUserProjectRoles(UUID userId, UUID projectId){
        List<UserRole> roles = userRoleRepository.findAllByUserIdAndProjectId(userId, projectId);
        List<RoleDto> ans = new ArrayList<>();
        for(UserRole role : roles){
            ans.add(makeRoleDto(role.getProjectRole()));
        }
        return ans;
    }

    private RoleDto makeRoleDto(ProjectRole role){
        return new RoleDto(role.getId(),
                role.getProjectId(),
                role.getName(),
                role.getColor(),
                role.getLikesAmount());
    }
}
