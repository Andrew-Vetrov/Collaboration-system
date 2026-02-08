import { suggestionsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getSuggestionsQueryKey } from '@/entities/suggestion/lib/getSuggestionsQueryKey';

export function useSuggestionDelete(project_id: string, suggestionId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      await suggestionsApi.suggestionsSuggestionIdDelete(suggestionId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getSuggestionsQueryKey(project_id),
      });
    },
  });
}
