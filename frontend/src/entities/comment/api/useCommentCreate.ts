import { commentsApi } from '@/shared/api';
import type { SuggestionsSuggestionIdCommentsPostRequest } from '@/shared/api/generated';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getCommentsQueryKey } from '../lib/getCommentsQueryKey';

export function useCommentCreate(suggestionId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: SuggestionsSuggestionIdCommentsPostRequest) => {
      const responce = await commentsApi.suggestionsSuggestionIdCommentsPost(
        suggestionId,
        data
      );
      return responce.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getCommentsQueryKey(suggestionId),
      });
    },
  });
}
