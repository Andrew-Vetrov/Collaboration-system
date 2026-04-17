import { getProjectUsersQueryKey } from '@/entities/project/lib/getProjectUsersQueryKey';
import { rolesApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';

interface UseDeleteUserRoleMutationFnProps {
  userId: string;
  roleId: string;
}

export const useDeleteUserRole = (projectId: string) => {
  const projectUserQueryKeys = getProjectUsersQueryKey(projectId);
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ roleId, userId }: UseDeleteUserRoleMutationFnProps) => {
      return rolesApi.projectsProjectIdUsersUserIdRolesRoleIdDelete(
        projectId,
        userId,
        roleId
      );
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: projectUserQueryKeys });
    },
  });
};
