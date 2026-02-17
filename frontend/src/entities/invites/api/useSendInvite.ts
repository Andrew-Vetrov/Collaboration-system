import { invitesApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getProjectInvitesQueryKey } from '../lib/getProjectInvitesQueryKey';

export function useSendInvite(projectId: string) {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (userEmail: string) => {
      const response = invitesApi.projectsProjectIdInvitesPost(
        projectId,
        userEmail
      );
      return response;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectInvitesQueryKey(projectId),
      });
    },
  });
}
