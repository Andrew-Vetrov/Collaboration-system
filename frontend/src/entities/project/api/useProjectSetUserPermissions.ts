import { projectsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectUsersQueryKey } from '../lib/getProjectUsersQueryKey';

interface useProjectSetUserPermissionsMutationProps {
  userId: string;
  setIsAdmin: boolean;
}

export function useProjectSetUserPermissions(projectId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      userId,
      setIsAdmin,
    }: useProjectSetUserPermissionsMutationProps) => {
      const responce = projectsApi.projectsProjectIdUsersUserIdPut(
        projectId,
        userId,
        { is_admin: setIsAdmin }
      );
      return responce;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectUsersQueryKey(projectId),
      });
    },
  });
}
