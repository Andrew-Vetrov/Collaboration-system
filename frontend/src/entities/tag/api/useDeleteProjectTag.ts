import { tagsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectTagsQueryKeys } from '../lib/getProjectTagsQueryKeys';
import { getSuggestionsQueryKey } from '@/entities/suggestion/lib/getSuggestionsQueryKey';

interface UseDeleteProjectTagMutationFnProps {
  tagId: string;
}

export const useDeleteProjectTag = (projectId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ tagId }: UseDeleteProjectTagMutationFnProps) => {
      return tagsApi.projectsProjectIdTagsTagIdDelete(projectId, tagId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectTagsQueryKeys(projectId),
      });
      queryClient.invalidateQueries({
        queryKey: getSuggestionsQueryKey(projectId),
      });
    },
  });
};
