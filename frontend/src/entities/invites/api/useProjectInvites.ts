import { useQuery } from '@tanstack/react-query';
import { getProjectInvitesQueryKey } from '../lib/getProjectInvitesQueryKey';
import { invitesApi } from '@/shared/api';
import type { Invite } from '../model/types';

export function useProjectInvites(projectId: string) {
  return useQuery<Invite[]>({
    queryKey: getProjectInvitesQueryKey(projectId),
    queryFn: async () => {
      const response = await invitesApi.projectsProjectIdInvitesGet(projectId);
      return response.data.data;
    },
  });
}
