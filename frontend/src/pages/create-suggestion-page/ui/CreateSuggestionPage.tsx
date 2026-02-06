import { useAuthMe } from '@/entities/main-user/api/useAuthMe';
import type { Suggestion } from '@/entities/suggestion';
import { useSuggestionCreate } from '@/entities/suggestion/api/useSuggestionCreate';
import { useSuggestionDelete } from '@/entities/suggestion/api/useSuggestionDelete';
import { useSuggestions } from '@/entities/suggestion/api/useSuggestions';
import { useSuggestionUpdate } from '@/entities/suggestion/api/useSuggestionUpdate';
import { SuggestionEditingFields } from '@/entities/suggestion/ui/SuggesstionEditingFields';
import { Button } from '@/shared/ui';
import { useState } from 'react';
import { Navigate, useParams } from 'react-router-dom';

export function CreateSuggestionPage() {
  const { id } = useParams<{ id: string }>();
  if (!id) {
    return <Navigate to="/not-found" replace />;
  }

  const { data: user } = useAuthMe();

  const { data: suggestions = [], isLoading, error } = useSuggestions(id);

  const [currentSuggestion, setCurrentSuggestion] = useState<Suggestion | null>(
    null
  );

  const { mutate: handleSuggestionUpdate } = useSuggestionUpdate(
    id,
    currentSuggestion?.suggestion_id ?? ''
  );

  const { mutate: handleSuggestionDelete } = useSuggestionDelete(
    id,
    currentSuggestion?.suggestion_id ?? ''
  );

  const { mutate: handleSuggestionCreate } = useSuggestionCreate(id);
  if (isLoading) {
    return <div>Загрузка...</div>;
  }

  if (error) {
    return <div>Ошибка загрузки...</div>;
  }
  return (
    <main className="flex">
      <div className="flex flex-col">
        <div className="font-bold">Список черновиков</div>
        {suggestions
          .filter(s => s.status === 'draft')
          .map(suggestion => (
            <div
              key={suggestion.suggestion_id}
              onClick={() => setCurrentSuggestion(suggestion)}
            >
              {suggestion.name}
            </div>
          ))}
        <Button
          onClick={() => {
            if (!user) return;

            handleSuggestionCreate(
              {
                name: 'Черновик без названия',
                description: 'Черновик без описания',
                user_id: user.user_id || '',
                status: 'draft',
              },
              {
                onSuccess: (newSuggestion: Suggestion) => {
                  console.log('set');
                  setCurrentSuggestion(newSuggestion);
                },
              }
            );
          }}
        >
          Создать черновик
        </Button>
      </div>
      {currentSuggestion && (
        <SuggestionEditingFields
          suggestion={currentSuggestion}
          handleSuggestionUpdate={handleSuggestionUpdate}
          handleSuggestionDelete={handleSuggestionDelete}
          onClose={setCurrentSuggestion}
        />
      )}
    </main>
  );
}
