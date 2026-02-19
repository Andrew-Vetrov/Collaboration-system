import { suggestionsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getSuggestionQueryKey } from '../lib/getSuggestionQueryKey';
import { getSuggestionsQueryKey } from '../lib/getSuggestionsQueryKey';
import { getProjectPermissionsQueryKey } from '@/entities/project/lib/getProjectPermissionsQueryKey';
import type { ProjectPermissions, Suggestion } from '@/shared/api/generated';

export function useSuggestionAddLike(suggestionId: string, projectId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () =>
      suggestionsApi.suggestionsSuggestionIdLikesPost(suggestionId),

    onMutate: async () => {
      const suggestionKey = getSuggestionQueryKey(suggestionId);
      const permissionsKey = getProjectPermissionsQueryKey(projectId);
      const suggestionsKey = getSuggestionsQueryKey(projectId);

      await Promise.all([
        queryClient.cancelQueries({ queryKey: suggestionKey }),
        queryClient.cancelQueries({ queryKey: suggestionsKey }),
        queryClient.cancelQueries({ queryKey: permissionsKey }),
      ]);

      const previousSuggestion =
        queryClient.getQueryData<Suggestion>(suggestionKey);

      const previousPermissions =
        queryClient.getQueryData<ProjectPermissions>(permissionsKey);

      if (!previousSuggestion || !previousPermissions) {
        return;
      }

      if (
        previousSuggestion.likes_amount == undefined ||
        previousSuggestion.user_likes_amount == undefined
      ) {
        return;
      }

      if (previousPermissions.likes_remain <= 0) {
        return;
      }

      queryClient.setQueryData<ProjectPermissions>(permissionsKey, {
        ...previousPermissions,
        likes_remain: previousPermissions.likes_remain - 1,
      });

      queryClient.setQueryData<Suggestion>(suggestionKey, {
        ...previousSuggestion,
        likes_amount: previousSuggestion.likes_amount + 1,
        user_likes_amount: previousSuggestion.user_likes_amount + 1,
      });

      return {
        previousSuggestion,
        previousPermissions,
      };
    },

    onError: (_error, _variables, context) => {
      if (!context) return;

      queryClient.setQueryData(
        getSuggestionQueryKey(suggestionId),
        context.previousSuggestion
      );

      queryClient.setQueryData(
        getProjectPermissionsQueryKey(projectId),
        context.previousPermissions
      );
    },

    onSettled: () => {
      queryClient.invalidateQueries({
        queryKey: getSuggestionQueryKey(suggestionId),
      });

      queryClient.invalidateQueries({
        queryKey: getSuggestionsQueryKey(projectId),
      });

      queryClient.invalidateQueries({
        queryKey: getProjectPermissionsQueryKey(projectId),
      });
    },
  });
}
