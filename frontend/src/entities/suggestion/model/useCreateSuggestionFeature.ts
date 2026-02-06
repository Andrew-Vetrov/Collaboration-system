import { useState } from 'react';
import { useSuggestionCreate } from '@/entities/suggestion/api/useSuggestionCreate';
import type { Suggestion } from '@/entities/suggestion';
import { useAuthMe } from '@/entities/main-user/api/useAuthMe';

export const useCreateSuggestionFeature = (projectId: string) => {
  const { data: user } = useAuthMe();
  const [currentSuggestion, setCurrentSuggestion] = useState<Suggestion | null>(
    null
  );

  const { mutate: create } = useSuggestionCreate(projectId);

  const handleCreate = () => {
    if (!user) return;

    create(
      {
        name: 'Черновик без названия',
        description: 'Черновик без описания',
        user_id: user.user_id || '',
        status: 'draft',
      },
      {
        onSuccess: (newSuggestion: Suggestion) => {
          setCurrentSuggestion(newSuggestion);
        },
      }
    );
  };

  return { handleCreate, currentSuggestion, setCurrentSuggestion };
};
