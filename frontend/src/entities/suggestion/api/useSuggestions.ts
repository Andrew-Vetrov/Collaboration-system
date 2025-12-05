import { useQuery } from '@tanstack/react-query';
import type { Suggestion } from '../model/types';
import { suggestionsApi } from '@/shared/api';

export const useSuggections = (projectId: string) => {
  return useQuery<Suggestion[]>({
    queryKey: ['suggestions', projectId],
    queryFn: async () => {
      const response =
        await suggestionsApi.projectProjectIdSuggestionsGet(projectId);
      return response.data.data ?? [];
    },
    enabled: !!projectId,
  });
};
