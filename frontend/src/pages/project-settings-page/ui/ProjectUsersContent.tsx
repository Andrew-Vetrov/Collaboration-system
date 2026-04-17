import { useAuthMe } from '@/entities/main-user/api/useAuthMe';
import { useProjectDeleteUser } from '@/entities/project/api/useProjectDeleteUser';
import { useProjectSetUserPermissions } from '@/entities/project/api/useProjectSetUserPermissions';
import { useProjectUsers } from '@/entities/project/api/useProjectUsers';
import { useDeleteUserRole } from '@/entities/role';
import { RoleBadge } from '@/entities/role/ui/RoleBadge';
import { RoleDropdownMenu } from '@/features/role-management';
import { Avatar, AvatarFallback, AvatarImage, Button } from '@/shared/ui';
import { Loader2 } from 'lucide-react';
import { Navigate } from 'react-router-dom';
import { useState } from 'react';

interface ProjectUsersContentProps {
  projectId: string;
}

export function ProjectUsersContent({ projectId }: ProjectUsersContentProps) {
  const {
    data,
    isLoading: usersLoading,
    isError: usersError,
  } = useProjectUsers(projectId);

  const [pendingRoleRemoval, setPendingRoleRemoval] = useState<
    Record<string, boolean>
  >({});
  const [pendingDeleteUserIds, setPendingDeleteUserIds] = useState<
    Record<string, boolean>
  >({});
  const [pendingPermissionUserIds, setPendingPermissionUserIds] = useState<
    Record<string, boolean>
  >({});

  const { mutateAsync: deleteUserRole } = useDeleteUserRole(projectId);

  const {
    data: currentUser,
    isLoading: meLoading,
    isError: meError,
  } = useAuthMe();
  const users = data?.users;

  const { mutateAsync: chagePermissions } =
    useProjectSetUserPermissions(projectId);

  const { mutateAsync: deleteUser } = useProjectDeleteUser(projectId);

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
                {user.roles?.map((role, index) => {
                  const pendingKey = `${user.user_id}-${role.role_id}`;
                  return (
                    <RoleBadge
                      key={`${user.user_id}-${role.role_id}-${index}`}
                      role={role}
                      onRemove={async roleId => {
                        setPendingRoleRemoval(prev => ({
                          ...prev,
                          [pendingKey]: true,
                        }));
                        try {
                          await deleteUserRole({
                            roleId: roleId,
                            userId: user.user_id,
                          });
                        } finally {
                          setPendingRoleRemoval(prev => {
                            const { [pendingKey]: _, ...rest } = prev;
                            return rest;
                          });
                        }
                      }}
                      disable={!!pendingRoleRemoval[pendingKey]}
                    />
                  );
                })}
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
                      <Button
                        variant="outline"
                        className="w-full h-7"
                        disabled={!!pendingDeleteUserIds[user.user_id]}
                        onClick={async () => {
                          setPendingDeleteUserIds(prev => ({
                            ...prev,
                            [user.user_id]: true,
                          }));
                          try {
                            await deleteUser({
                              userId: user.user_id,
                              isCurrentUser: false,
                            });
                          } finally {
                            setPendingDeleteUserIds(prev => {
                              const { [user.user_id]: _, ...rest } = prev;
                              return rest;
                            });
                          }
                        }}
                      >
                        Удалить из проекта
                      </Button>
                      {user.is_admin ? (
                        <Button
                          variant="outline"
                          className="w-full h-7"
                          onClick={async () => {
                            setPendingPermissionUserIds(prev => ({
                              ...prev,
                              [user.user_id]: true,
                            }));
                            try {
                              await chagePermissions({
                                userId: user.user_id,
                                setIsAdmin: false,
                              });
                            } finally {
                              setPendingPermissionUserIds(prev => {
                                const { [user.user_id]: _, ...rest } = prev;
                                return rest;
                              });
                            }
                          }}
                          disabled={!!pendingPermissionUserIds[user.user_id]}
                        >
                          Убрать админку
                        </Button>
                      ) : (
                        <Button
                          variant="outline"
                          className="w-full h-7"
                          onClick={async () => {
                            setPendingPermissionUserIds(prev => ({
                              ...prev,
                              [user.user_id]: true,
                            }));
                            try {
                              await chagePermissions({
                                userId: user.user_id,
                                setIsAdmin: true,
                              });
                            } finally {
                              setPendingPermissionUserIds(prev => {
                                const { [user.user_id]: _, ...rest } = prev;
                                return rest;
                              });
                            }
                          }}
                          disabled={!!pendingPermissionUserIds[user.user_id]}
                        >
                          Назначить админом
                        </Button>
                      )}
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
