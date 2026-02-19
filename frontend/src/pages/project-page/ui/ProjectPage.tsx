import type { JSX } from 'react';
import { useParams, Navigate, Link, useNavigate } from 'react-router-dom';
import { SuggestionsList } from '@/entities/suggestion/ui/SuggestionsList';
import { Button } from '@/shared/ui';
import { EllipsisVertical, Plus } from 'lucide-react';
import { routes } from '@/shared/route';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/shared/ui';
import { useProjectPermissions } from '@/entities/project/api/useProjectPermissions';
import { useAuthMe } from '@/entities/main-user/api/useAuthMe';
import { useProjectDeleteUser } from '@/entities/project/api/useProjectDeleteUser';

const ProjectPage = (): JSX.Element => {
  const { projectId } = useParams<{ projectId: string }>();
  if (!projectId) {
    return <Navigate to="/not-found" replace />;
  }

  const { data: permissions } = useProjectPermissions(projectId);
  const { data: currentUser } = useAuthMe();

  const { mutate: deleteUserMutation } = useProjectDeleteUser(projectId);
  const navigate = useNavigate();
  const handleLeaveProject = () => {
    if (!currentUser?.user_id) return;

    deleteUserMutation(
      { userId: currentUser.user_id, isCurrentUser: true },
      {
        onSuccess: () => {
          navigate(routes.projectsRoute());
        },
      }
    );
  };

  const isAdmin = permissions?.is_admin || false;

  return (
    <main className="min-h-screen flex justify-center py-4">
      <div className="w-full grid grid-cols-[auto_auto] grid-rows-[auto_1fr] sm:grid-cols-[auto_1fr_auto] max-w-5xl sm:gap-x-8 px-4">
        <div className="sm:order-1">
          <Link to={routes.createSuggestionRoute(projectId)}>
            <Button variant="outline" size="icon" className="size-8 sm:size-10">
              <Plus className="size-8 sm:size-10 " />
            </Button>
          </Link>
        </div>
        <div className="col-span-2 sm:col-auto order-3  sm:order-2">
          <SuggestionsList projectId={projectId} />
        </div>
        <DropdownMenu>
          <DropdownMenuTrigger className="order-2 sm:order-3 size-8 sm:size-10 justify-self-end">
            <EllipsisVertical className="size-8 sm:size-10 " />
          </DropdownMenuTrigger>
          <DropdownMenuContent className="">
            {isAdmin && (
              <DropdownMenuItem>
                <Link
                  to={routes.projectSettingsRoute(projectId)}
                  className="w-full"
                >
                  <Button variant="outline" className="w-full">
                    Настройки
                  </Button>
                </Link>
              </DropdownMenuItem>
            )}
            <DropdownMenuItem>
              <Button onClick={handleLeaveProject}>Выйти из проекта</Button>
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </main>
  );
};

export default ProjectPage;
