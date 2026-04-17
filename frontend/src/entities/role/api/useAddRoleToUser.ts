import { useMutation, useQueryClient } from '@tanstack/react-query';
import { rolesApi } from '@/shared/api';
import { getProjectUsersQueryKey } from '@/entities/project/lib/getProjectUsersQueryKey';

export const useAddRoleToUser = (projectId: string, userId: string) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (role_id: string) => {
      return rolesApi.projectsProjectIdUsersUserIdRolesPost(projectId, userId, {
        role_id: role_id,
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectUsersQueryKey(projectId),
      });
    },
  });
};
