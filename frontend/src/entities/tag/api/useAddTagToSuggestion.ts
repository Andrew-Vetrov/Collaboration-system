import { getSuggestionQueryKey } from '@/entities/suggestion/lib/getSuggestionQueryKey';
import { getSuggestionsQueryKey } from '@/entities/suggestion/lib/getSuggestionsQueryKey';
import { tagsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';

export const useAddTagToSuggestion = (projectId: string, suggestionId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ tagId }: { tagId: string }) => {
      return tagsApi.suggestionsSuggestionIdTagsPost(suggestionId, {
        tag_id: tagId,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getSuggestionsQueryKey(projectId),
      });
      queryClient.invalidateQueries({
        queryKey: getSuggestionQueryKey(suggestionId),
      });
    },
  });
};
