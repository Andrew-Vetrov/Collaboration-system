import { suggestionsApi } from '@/shared/api';
import type {
  ProjectProjectIdSuggestionsPostRequest,
  Suggestion,
} from '@/shared/api/generated';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getSuggestionsQueryKey } from '../lib/getSuggestionsQueryKey';

export function useSuggestionCreate(project_id: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (
      data: ProjectProjectIdSuggestionsPostRequest
    ): Promise<Suggestion> => {
      console.log('create suggestion');
      const response = await suggestionsApi.projectProjectIdSuggestionsPost(
        project_id,
        data
      );
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getSuggestionsQueryKey(project_id),
      });
    },
  });
}
