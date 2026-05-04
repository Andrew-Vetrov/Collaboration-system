import { rolesApi } from '@/shared/api';
import type { ProjectsProjectIdRolesPostRequest } from '@/shared/api/generated';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectRolesQueryKeys } from '../lib/getProjectRolesQueryKey';

export const useCreateProjectRole = (projectId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: ProjectsProjectIdRolesPostRequest) => {
      return rolesApi.projectsProjectIdRolesPost(projectId, data);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectRolesQueryKeys(projectId),
      });
    },
  });
};
