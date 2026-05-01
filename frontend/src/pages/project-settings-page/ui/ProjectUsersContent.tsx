import { useAuthMe } from '@/entities/main-user/api/useAuthMe';
import { useProjectUsers } from '@/entities/project/api/useProjectUsers';
import { RoleBadge } from '@/entities/role/ui/RoleBadge';
import {
  RoleDropdownMenu,
  useRoleDeletion,
  useSetUserPermissions,
  DeleteRoleButton,
  PermissionButton,
} from '@/features/role-management';
import {
  useDeleteUserFromProject,
  DeleteUserButton,
} from '@/features/project-delete-user';
import { Avatar, AvatarFallback, AvatarImage } from '@/shared/ui';
import { Loader2 } from 'lucide-react';
import { Navigate } from 'react-router-dom';

interface ProjectUsersContentProps {
  projectId: string;
}

export function ProjectUsersContent({ projectId }: ProjectUsersContentProps) {
  const {
    data,
    isLoading: usersLoading,
    isError: usersError,
  } = useProjectUsers(projectId);

  const { handleDeleteRole, isRoleRemovalPending } = useRoleDeletion(projectId);
  const { handleDeleteUser, isDeleteUserPending } =
    useDeleteUserFromProject(projectId);
  const { handlePermissionChange, isPermissionChangePending } =
    useSetUserPermissions(projectId);

  const {
    data: currentUser,
    isLoading: meLoading,
    isError: meError,
  } = useAuthMe();
  const users = data?.users;

  if (usersLoading || meLoading) {
    return (
      <div className="flex h-[50vh] items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-primary" />
      </div>
    );
  }

  if (usersError || meError || !users) {
    return <Navigate to="/not-found" replace />;
  }

  return (
    <div className="flex flex-col gap-4">
      <div className="ml-4 text-xl">Список пользователей проекта:</div>
      <ul className="flex flex-col gap-3 max-w-full">
        {users.map(user => (
          <li key={user.user_id}>
            <div className="flex flex-col rounded-lg border bg-card p-2 max-w-full gap-2">
              <div className="flex flex-wrap gap-2 min-w-0">
                {user.roles?.map((role, index) => (
                  <RoleBadge
                    key={`${user.user_id}-${role.role_id}-${index}`}
                    role={role}
                    onRemove={roleId => handleDeleteRole(roleId, user.user_id)}
                    disable={isRoleRemovalPending(user.user_id, role.role_id)}
                  />
                ))}
              </div>
              <div className="flex flex-col sm:flex-row gap-2">
                <div className="flex w-full gap-2">
                  <div className="pt-2">
                    <Avatar className="h-9 w-9 min-w-9 shrink-0 ">
                      <AvatarImage src={user.avatar_url} className="" />
                      <AvatarFallback>
                        {user.nickname?.slice(0, 2).toUpperCase()}
                      </AvatarFallback>
                    </Avatar>
                  </div>
                  <div className="flex flex-col gap-1 w-full">
                    <div>{user.email}</div>
                    <div>{user.nickname}</div>
                  </div>
                </div>
                <div className="flex flex-col gap-1">
                  <RoleDropdownMenu projectId={projectId} user={user} />
                  {user.user_id !== currentUser?.user_id && (
                    <>
                      <DeleteUserButton
                        userId={user.user_id}
                        isLoading={isDeleteUserPending(user.user_id)}
                        onDelete={handleDeleteUser}
                      />
                      <PermissionButton
                        userId={user.user_id}
                        isAdmin={user.is_admin}
                        isLoading={isPermissionChangePending(user.user_id)}
                        onPermissionChange={handlePermissionChange}
                      />
                    </>
                  )}
                </div>
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}
