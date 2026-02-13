import { commentsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getCommentsQueryKey } from '../lib/getCommentsQueryKey';

export function useCommentDelete(suggestionId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (commentId: string) => {
      const response = commentsApi.commentsCommentIdDelete(commentId);
      return response;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getCommentsQueryKey(suggestionId),
      });
    },
  });
}
