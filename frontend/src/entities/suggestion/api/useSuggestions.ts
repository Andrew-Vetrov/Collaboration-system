import { useQuery } from '@tanstack/react-query';
import type { Suggestion } from '../model/types';
import { suggestionsApi } from '@/shared/api';
import { getSuggestionsQueryKey } from '../lib/getSuggestionsQueryKey';

export const useSuggestions = (projectId: string, tagId?: string) => {
  return useQuery<Suggestion[]>({
    queryKey: [...getSuggestionsQueryKey(projectId), tagId],
    queryFn: async () => {
      const response = await suggestionsApi.projectProjectIdSuggestionsGet(
        projectId,
        undefined,
        tagId ? [tagId] : undefined
      );
      return response.data.data ?? [];
    },
    enabled: !!projectId,
  });
};
