import type { JSX } from 'react';
import { useParams, Navigate, Link } from 'react-router-dom';
import { SuggestionsList } from '@/entities/suggestion/ui/SuggestionsList';
import { Button } from '@/shared/ui';
import { Plus } from 'lucide-react';
import { routes } from '@/shared/route';

const ProjectPage = (): JSX.Element => {
  const { projectId: id } = useParams<{ projectId: string }>();
  if (!id) {
    return <Navigate to="/not-found" replace />;
  }

  return (
    <main className="relative min-h-screen flex flex-col">
      <div className="flex-1 flex items-start justify-center py-4">
        <div className="w-full max-w-5xl flex items-start justify-between gap-8 px-4">
          <div className="shrink-0">
            <Link to={routes.createSuggestionRoute(id)}>
              <Button variant="outline" size="icon" className="size-10">
                <Plus className="size-10 rounded-md " />
              </Button>
            </Link>
          </div>
          <SuggestionsList projectId={id} />
          <div className="shrink-0 w-12" />
        </div>
      </div>
    </main>
  );
};

export default ProjectPage;
