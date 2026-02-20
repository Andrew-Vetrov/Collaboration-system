import { suggestionsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getSuggestionQueryKey } from '../lib/getSuggestionQueryKey';
import { getProjectPermissionsQueryKey } from '@/entities/project/lib/getProjectPermissionsQueryKey';
import { getSuggestionsQueryKey } from '../lib/getSuggestionsQueryKey';
import type { Suggestion } from '../model/types';
import type { ProjectPermissions } from '@/shared/api/generated';

export function useSuggestionRemoveLike(
  suggestionId: string,
  projectId: string
) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () =>
      suggestionsApi.suggestionsSuggestionIdLikesDelete(suggestionId),
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

      queryClient.setQueryData<ProjectPermissions>(permissionsKey, {
        ...previousPermissions,
        likes_remain: previousPermissions.likes_remain + 1,
      });

      queryClient.setQueryData<Suggestion>(suggestionKey, {
        ...previousSuggestion,
        likes_amount: Math.max(0, previousSuggestion.likes_amount - 1),
        user_likes_amount: Math.max(
          0,
          previousSuggestion.user_likes_amount - 1
        ),
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
