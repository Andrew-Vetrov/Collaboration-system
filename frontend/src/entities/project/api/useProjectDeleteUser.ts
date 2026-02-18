import { projectsApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectsQueryKey } from '../lib/getProjectsQueryKey';
import { getProjectPermissionsQueryKey } from '../lib/getProjectPermissionsQueryKey';
import { getProjectUsersQueryKey } from '../lib/getProjectUsersQueryKey';

interface ProjectDeleteUserMutationFunctionProps {
  userId: string;
  isCurrentUser: boolean;
}

export function useProjectDeleteUser(projectId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      userId,
      isCurrentUser = false,
    }: ProjectDeleteUserMutationFunctionProps) => {
      return projectsApi.projectsProjectIdUsersUserIdDelete(projectId, userId);
    },
    onSuccess: (_data, variables) => {
      if (variables.isCurrentUser) {
        queryClient.invalidateQueries({ queryKey: getProjectsQueryKey() });
        queryClient.invalidateQueries({
          queryKey: getProjectPermissionsQueryKey(projectId),
        });
      }
      queryClient.invalidateQueries({
        queryKey: getProjectUsersQueryKey(projectId),
      });
    },
  });
}
