import { useAuthMe } from '@/entities/main-user/api/useAuthMe';
import { useProjectDeleteUser } from '@/entities/project/api/useProjectDeleteUser';
import { useProjectSetUserPermissions } from '@/entities/project/api/useProjectSetUserPermissions';
import { useProjectUsers } from '@/entities/project/api/useProjectUsers';
import { Avatar, AvatarFallback, AvatarImage, Button } from '@/shared/ui';
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

  const {
    data: currentUser,
    isLoading: meLoading,
    isError: meError,
  } = useAuthMe();
  const users = data?.users;

  const { mutate: chagePermissions, isPending: changePermissionPending } =
    useProjectSetUserPermissions(projectId);

  const { mutate: deleteUser, isPending: deleteUserPending } =
    useProjectDeleteUser(projectId);

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
      <ul className="flex flex-col gap-3">
        {users
          .filter(p => p.user_id != currentUser?.user_id)
          .map((user, index) => (
            <li key={user.user_id}>
              <div className="flex flex-col sm:flex-row gap-2 rounded-lg border bg-card p-2">
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
                  <Button
                    variant="outline"
                    className="w-full h-7"
                    disabled={deleteUserPending}
                    onClick={() =>
                      deleteUser({ userId: user.user_id, isCurrentUser: false })
                    }
                  >
                    Удалить из проекта
                  </Button>
                  {user.is_admin ? (
                    <Button
                      variant="outline"
                      className="w-full h-7"
                      onClick={() =>
                        chagePermissions({
                          userId: user.user_id,
                          setIsAdmin: false,
                        })
                      }
                      disabled={changePermissionPending}
                    >
                      Убрать админку
                    </Button>
                  ) : (
                    <Button
                      variant="outline"
                      className="w-full h-7"
                      onClick={() =>
                        chagePermissions({
                          userId: user.user_id,
                          setIsAdmin: true,
                        })
                      }
                      disabled={changePermissionPending}
                    >
                      Назначить админом
                    </Button>
                  )}
                </div>
              </div>
            </li>
          ))}
      </ul>
    </div>
  );
}
