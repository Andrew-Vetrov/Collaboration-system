import { Navigate, useParams } from 'react-router-dom';
import { useSuggestions } from '../api/useSuggestions';
import type { Suggestion } from '../model/types';

type DraftsListProps = {
  className?: string;
  clickSuggestion: (suggestion: Suggestion) => void;
};

export function DraftsList({ clickSuggestion }: DraftsListProps) {
  const { projectId: id } = useParams<{ projectId: string }>();
  if (!id) {
    return <Navigate to="/not-found" replace />;
  }
  const { data: suggestions = [], isLoading, error } = useSuggestions(id);

  if (isLoading) {
    return <div>Загрузка...</div>;
  }

  if (error) {
    return <div>Ошибка загрузки...</div>;
  }

  return (
    <div className="flex flex-col gap-4">
      {suggestions
        .filter(s => s.status === 'draft')
        .map(suggestion => (
          <div
            key={suggestion.suggestion_id}
            onClick={() => clickSuggestion(suggestion)}
            className="cursor-pointer hover:bg-accent hover:text-accent-foreground border p-3 transition outline rounded-xl text-left w-full "
          >
            {suggestion.name}
          </div>
        ))}
    </div>
  );
}
