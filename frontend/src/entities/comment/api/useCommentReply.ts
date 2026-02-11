import { commentsApi } from '@/shared/api';
import type { CommentIdReplyPostRequest } from '@/shared/api/generated';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getCommentsQueryKey } from '../lib/getCommentsQueryKey';

export function useCommentReply(suggestionId: string, commentId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: CommentIdReplyPostRequest) => {
      const responce = await commentsApi.commentIdReplyPost(commentId, data);
      return responce.data;
    },

    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getCommentsQueryKey(suggestionId),
      });
    },
  });
}
