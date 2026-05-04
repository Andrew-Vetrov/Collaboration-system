import { rolesApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectRolesQueryKeys } from '../lib/getProjectRolesQueryKey';

export const useUpdateRoleSettings = (projectId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      roleId,
      likesAmount,
    }: {
      roleId: string;
      likesAmount: string;
    }) => {
      return rolesApi.projectsProjectIdRolesRoleIdLikesPut(projectId, roleId, {
        likes_amount: parseInt(likesAmount),
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectRolesQueryKeys(projectId),
      });
    },
  });
};
