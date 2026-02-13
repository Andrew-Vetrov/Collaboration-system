import { useQuery } from '@tanstack/react-query';
import { getCommentsQueryKey } from '../lib/getCommentsQueryKey';
import { commentsApi } from '@/shared/api';
import type { Comment } from '../model/types';

export function useComments(suggestionId: string) {
  return useQuery<Comment[]>({
    queryKey: getCommentsQueryKey(suggestionId),
    queryFn: async () => {
      const response =
        await commentsApi.suggestionsSuggestionIdCommentsGet(suggestionId);

      return response.data;
    },
  });
}
