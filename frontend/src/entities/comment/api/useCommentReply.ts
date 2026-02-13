import { commentsApi } from '@/shared/api';
import {
  type CurrentUser,
  type CommentIdReplyPostRequest,
} from '@/shared/api/generated';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getCommentsQueryKey } from '../lib/getCommentsQueryKey';
import type { Comment } from '../model/types';
import { getAuthMeQueryKey } from '@/entities/main-user/lib/getAuthMeQueryKey';

export function useCommentReply(suggestionId: string, commentId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (data: CommentIdReplyPostRequest) => {
      const response = await commentsApi.commentIdReplyPost(commentId, data);
      return response.data;
    },

    onMutate: async (data: CommentIdReplyPostRequest) => {
      queryClient.cancelQueries({
        queryKey: getCommentsQueryKey(suggestionId),
      });
      const prevCommentsData =
        queryClient.getQueryData<Comment[]>(
          getCommentsQueryKey(suggestionId)
        ) ?? [];
      const currentUserId =
        queryClient.getQueryData<CurrentUser>(getAuthMeQueryKey())?.user_id;
      const now = new Date().toISOString();
      const optimisticComment: Comment = {
        comment_id: `temp-${crypto.randomUUID()}`,
        user_id: currentUserId?.toString() || '',
        suggestion_id: suggestionId,
        text: data.text,
        comment_reply_to_id: commentId,
        placed_at: now,
        last_edit: now,
      };
      queryClient.setQueryData<Comment[]>(getCommentsQueryKey(suggestionId), [
        ...prevCommentsData,
        optimisticComment,
      ]);
      return { prevCommentsData, optimisticComment };
    },

    onError: (error, vars, context) => {
      if (!context) return;
      queryClient.setQueryData<Comment[]>(
        getCommentsQueryKey(suggestionId),
        context.prevCommentsData
      );
    },

    onSuccess: (newComment, vars, context) => {
      if (!context) return;
      queryClient.setQueryData<Comment[]>(
        getCommentsQueryKey(suggestionId),
        context.prevCommentsData.map(c =>
          c.comment_id === context.optimisticComment.comment_id ? newComment : c
        )
      );
    },

    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: getCommentsQueryKey(suggestionId),
      });
    },
  });
}
