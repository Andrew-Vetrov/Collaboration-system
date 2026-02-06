import { Navigate, useParams } from 'react-router-dom';
import { useSuggestions } from '../api/useSuggestions';
import type { Suggestion } from '../model/types';

type DraftsListProps = {
  clickSuggestion: (suggestion: Suggestion) => void;
};

export function DraftsList({ clickSuggestion }: DraftsListProps) {
  const { id } = useParams<{ id: string }>();
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
    <>
      {suggestions
        .filter(s => s.status === 'draft')
        .map(suggestion => (
          <div
            key={suggestion.suggestion_id}
            onClick={() => clickSuggestion(suggestion)}
          >
            {suggestion.name}
          </div>
        ))}
    </>
  );
}
