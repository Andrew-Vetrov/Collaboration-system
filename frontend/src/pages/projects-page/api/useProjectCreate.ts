import type { ProjectBasic } from '@/entities/project';
import type { ProjectsPostRequest } from '@/entities/project';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { projectsApi } from '@/shared/api';

export const useProjectCreate = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: ProjectsPostRequest): Promise<ProjectBasic> => {
      const response = await projectsApi.projectsPost(data);
      if (!response.data.project) {
        throw new Error('Server response does not contain project');
      }
      return response.data.project;
    },

    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['projects'] });
    },
  });
};
