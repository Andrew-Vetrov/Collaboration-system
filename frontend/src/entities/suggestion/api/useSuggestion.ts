import { useQuery } from '@tanstack/react-query';
import { suggestionsApi } from '@/shared/api';
import type { Suggestion } from '../model/types';

export const useSuggestion = (suggestionId: string | undefined) => {
  return useQuery<Suggestion | null>({
    queryKey: ['suggestion', suggestionId],
    queryFn: async () => {
      if (!suggestionId) return null;
      const response =
        await suggestionsApi.suggestionsSuggestionIdGet(suggestionId);
      console.log('response', response.data);
      return response.data ?? null;
    },
    enabled: !!suggestionId,
    retry: false,
  });
};
