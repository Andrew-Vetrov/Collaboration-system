import { useProjects } from '@/entities/project';
import { useProjectPermissions } from '@/entities/project/api/useProjectPermissions';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/shared/ui/tabs';
import { Loader2 } from 'lucide-react';
import { Navigate, useParams } from 'react-router-dom';
import { ProjectSettingsContent } from './ProjectSettingsContent';
import { useMediaQuery } from '@/shared/hooks/useMediaQuery';
import { cn } from '@/shared/lib/utils';
import { ProjectUsersContent } from './ProjectUsersContent';
import { ProjectInvitesContent } from './ProjectInvitesContent';

const ProjectSettingsPage = () => {
  const { projectId } = useParams<{ projectId: string }>();

  const {
    data: projects,
    isLoading: projectsLoading,
    isError: projectsError,
  } = useProjects();
  const {
    data: permissions,
    isLoading: permLoading,
    isError: permError,
  } = useProjectPermissions(projectId);

  const isMobile = useMediaQuery('(max-width: 640px)');

  if (projectsLoading || permLoading) {
    return (
      <div className="flex h-[50vh] items-center justify-center">
        <Loader2 className="h-10 w-10 animate-spin text-primary" />
      </div>
    );
  }

  if (projectsError || permError || !projects) {
    return <Navigate to="/not-found" replace />;
  }

  const currentProject = projects.find(p => p.project_id === projectId);
  if (!currentProject) {
    return <Navigate to="/not-found" replace />;
  }

  if (!permissions?.is_admin) {
    return <Navigate to="/not-found" replace />;
  }

  return (
    <main className="flex flex-col items-center ">
      <div className="text-center py-4 text-3xl">
        {`Настройки для проекта ${currentProject.name}`}
      </div>
      <Tabs
        defaultValue="settings"
        orientation={isMobile ? 'horizontal' : 'vertical'}
        className="max-w-5xl w-full flex sm:flex-row flex-col px-4 gap-4"
      >
        <div className={cn('', isMobile ? 'overflow-x-auto w-full pb-2' : '')}>
          <TabsList variant="line">
            <TabsTrigger value="settings">Настройки проекта</TabsTrigger>
            <TabsTrigger value="users">Пользователи проекта</TabsTrigger>
            <TabsTrigger value="invites">Приглашения</TabsTrigger>
          </TabsList>
        </div>
        <TabsContent value="settings">
          <ProjectSettingsContent projectId={projectId} />
        </TabsContent>
        <TabsContent value="users">
          <ProjectUsersContent projectId={projectId} />
        </TabsContent>
        <TabsContent value="invites">
          <ProjectInvitesContent projectId={projectId} />
        </TabsContent>
      </Tabs>
    </main>
  );
};

export default ProjectSettingsPage;
