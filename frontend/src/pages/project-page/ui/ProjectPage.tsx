import { Header } from '@/widgets/header';
import type { JSX } from 'react';
import { useParams, Navigate } from 'react-router-dom';
import { SuggestionsList } from '@/entities/suggestion/ui/SuggestionsList';

const ProjectPage = (): JSX.Element => {
  const { id } = useParams<{ id: string }>();
  if (!id) {
    return <Navigate to="/not-found" replace />;
  }

  return (
    <>
      <Header />
      <SuggestionsList projectId={id} />
    </>
  );
};

export default ProjectPage;
