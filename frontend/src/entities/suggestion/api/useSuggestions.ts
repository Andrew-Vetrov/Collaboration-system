import { useQuery } from '@tanstack/react-query';
import type { Suggestion } from '../model/types';
import { suggestionsApi } from '@/shared/api';
import { getSuggestionsQueryKey } from '../lib/getSuggestionsQueryKey';

export const useSuggestions = (projectId: string) => {
  return useQuery<Suggestion[]>({
    queryKey: getSuggestionsQueryKey(projectId),
    queryFn: async () => {
      const response =
        await suggestionsApi.projectProjectIdSuggestionsGet(projectId);
      return response.data.data ?? [];
    },
    enabled: !!projectId,
  });
};
