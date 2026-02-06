import { suggestionsApi } from '@/shared/api';
import type { SuggestionsSuggestionIdPutRequest } from '@/shared/api/generated';
import type { Suggestion } from '../model/types';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getSuggestionsQueryKey } from '../lib/getSuggestionsQueryKey';

export function useSuggestionUpdate(project_id: string, suggestionId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (
      data: SuggestionsSuggestionIdPutRequest
    ): Promise<Suggestion> => {
      console.log('handleSuggestionUpdate');
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
    },
  });
}
