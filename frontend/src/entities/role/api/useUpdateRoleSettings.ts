import { rolesApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectRolesQueryKeys } from '../lib/getProjectRolesQueryKey';
import { getProjectUsersQueryKey } from '@/entities/project/lib/getProjectUsersQueryKey';

export const useUpdateRoleSettings = (projectId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({
      roleId,
      likesAmount,
      color,
      name,
    }: {
      roleId: string;
      likesAmount: string;
      color: string;
      name: string;
    }) => {
      const response = await rolesApi.projectsProjectIdRolesRoleIdPut(
        projectId,
        roleId,
        {
          likes_amount: parseInt(likesAmount),
          color: color,
          name: name,
        }
      );
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectRolesQueryKeys(projectId),
      });
      queryClient.invalidateQueries({
        queryKey: getProjectUsersQueryKey(projectId),
      });
    },
  });
};
