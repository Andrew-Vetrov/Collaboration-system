import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { ProjectsProjectIdTagsPostRequest } from '../model/types';
import { tagsApi } from '@/shared/api';
import { getProjectTagsQueryKeys } from '../lib/getProjectTagsQueryKeys';

export const useCreateProjectTag = (projectId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: ProjectsProjectIdTagsPostRequest) => {
      return tagsApi.projectsProjectIdTagsPost(projectId, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectTagsQueryKeys(projectId),
      });
    },
  });
};
