import { ProjectInfo } from '@/entities/project';
import { useParams, Navigate } from 'react-router-dom';

export const ProjectPage = (): JSX.Element => {
  const { id } = useParams<{ id: string }>();
  if (!id) {
    return <Navigate to="/not-found" replace />;
  }

  return <ProjectInfo id={id} />;
};
