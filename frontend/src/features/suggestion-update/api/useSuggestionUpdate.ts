import { suggestionsApi } from '@/shared/api';
import type { SuggestionsSuggestionIdPutRequest } from '@/shared/api/generated';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { Suggestion } from '@/entities/suggestion/model/types';
import { getSuggestionsQueryKey } from '@/entities/suggestion/lib/getSuggestionsQueryKey';
import { getSuggestionQueryKey } from '@/entities/suggestion/lib/getSuggestionQueryKey';

export function useSuggestionUpdate(project_id: string, suggestionId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (
      data: SuggestionsSuggestionIdPutRequest
    ): Promise<Suggestion> => {
      const response = await suggestionsApi.suggestionsSuggestionIdPut(
        suggestionId,
        data
      );
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getSuggestionsQueryKey(project_id),
      });
      queryClient.invalidateQueries({
        queryKey: getSuggestionQueryKey(suggestionId),
      });
    },
  });
}
