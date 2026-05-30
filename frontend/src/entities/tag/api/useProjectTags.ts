import { useQuery } from '@tanstack/react-query';
import type { Tag } from '../model/types';
import { getProjectTagsQueryKeys } from '../lib/getProjectTagsQueryKeys';
import { tagsApi } from '@/shared/api';

export const useProjectTags = (projectId: string) => {
  return useQuery<Tag[]>({
    queryKey: getProjectTagsQueryKeys(projectId),
    queryFn: async () => {
      const response = await tagsApi.projectsProjectIdTagsGet(projectId);
      return response.data.data ?? [];
    },
  });
};
