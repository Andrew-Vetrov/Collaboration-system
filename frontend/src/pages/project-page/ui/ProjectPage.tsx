import type { JSX } from 'react';
import { useParams, Navigate } from 'react-router-dom';
import { SuggestionsList } from '@/entities/suggestion/ui/SuggestionsList';

const ProjectPage = (): JSX.Element => {
  const { id } = useParams<{ id: string }>();
  if (!id) {
    return <Navigate to="/not-found" replace />;
  }

  return (
    <main className="relative min-h-screen flex flex-col">
      <div className="flex-1 flex items-start justify-center">
        <div className="w-full max-w-5xl flex items-start justify-between gap-8 px-4">
          <SuggestionsList projectId={id} />
        </div>
      </div>
    </main>
  );
};

export default ProjectPage;
