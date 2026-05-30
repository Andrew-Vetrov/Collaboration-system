import { tagsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectTagsQueryKeys } from '../lib/getProjectTagsQueryKeys';
import { getSuggestionsQueryKey } from '@/entities/suggestion/lib/getSuggestionsQueryKey';

export const useUpdateProjectTag = (projectId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({
      color,
      name,
      tagId,
    }: {
      tagId: string;
      color: string;
      name: string;
    }) => {
      const response = await tagsApi.projectsProjectIdTagsTagIdPut(
        projectId,
        tagId,
        {
          color: color,
          name: name,
        }
      );
      return response.data;
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
