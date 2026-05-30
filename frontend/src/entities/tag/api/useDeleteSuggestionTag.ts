import { getSuggestionQueryKey } from '@/entities/suggestion/lib/getSuggestionQueryKey';
import { getSuggestionsQueryKey } from '@/entities/suggestion/lib/getSuggestionsQueryKey';
import { tagsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface UseDeleteSuggestionTagMutationFnProps {
  suggestionId: string;
  tagId: string;
}

export const useDeleteSuggestionTag = (
  projectId: string,
  suggestionId: string
) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      suggestionId,
      tagId,
    }: UseDeleteSuggestionTagMutationFnProps) => {
      return tagsApi.suggestionsSuggestionIdTagsTagIdDelete(
        suggestionId,
        tagId
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getSuggestionQueryKey(suggestionId),
      });
      queryClient.invalidateQueries({
        queryKey: getSuggestionsQueryKey(projectId),
      });
    },
  });
};
