import { useSuggestion } from '@/entities/suggestion';
import { Outlet, useParams, Navigate } from 'react-router-dom';

export const ProjectLayout = () => {
  const { projectId } = useParams<{ projectId: string }>();

  if (!projectId) {
    return <Navigate to="/not-found" replace />;
  }

  const { data: project, isLoading, isError } = useSuggestion(projectId);

  if (isLoading) {
    return <div>Загрузка проекта...</div>;
  }

  if (isError || !project) {
    return <Navigate to="/not-found" replace />;
  }

  return <Outlet />;
};
