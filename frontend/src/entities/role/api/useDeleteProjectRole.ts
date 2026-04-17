import { getProjectUsersQueryKey } from '@/entities/project/lib/getProjectUsersQueryKey';
import { rolesApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectRolesQueryKeys } from '../lib/getProjectRolesQueryKey';

interface UseDeleteProjectRoleMutationFnProps {
  roleId: string;
}

export const useDeleteProjectRole = (projectId: string) => {
  const projectUserQueryKeys = getProjectUsersQueryKey(projectId);
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ roleId }: UseDeleteProjectRoleMutationFnProps) => {
      return rolesApi.projectsProjectIdRolesRoleIdDelete(projectId, roleId);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: projectUserQueryKeys });
      queryClient.invalidateQueries({
        queryKey: getProjectRolesQueryKeys(projectId),
      });
    },
  });
};
