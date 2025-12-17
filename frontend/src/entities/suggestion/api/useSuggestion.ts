import { useQuery } from '@tanstack/react-query';
import { suggestionsApi } from '@/shared/api';
import type { Suggestion } from '../model/types';
import { getSuggestionQueryKey } from '../lib/getSuggestionQueryKey';

export const useSuggestion = (suggestionId: string | undefined) => {
  return useQuery<Suggestion | null>({
    queryKey: getSuggestionQueryKey(suggestionId),
    queryFn: async () => {
      if (!suggestionId) return null;
      const response =
        await suggestionsApi.suggestionsSuggestionIdGet(suggestionId);
      return response.data ?? null;
    },
    enabled: !!suggestionId,
    retry: false,
  });
};
