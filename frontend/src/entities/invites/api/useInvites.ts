import { useQuery } from '@tanstack/react-query';
import { getInvitesQueryKey } from '../lib/getInviteQueryKey';
import { invitesApi } from '@/shared/api';
import type { Invite } from '../model/types';

export function useInvites() {
  return useQuery<Invite[]>({
    queryKey: getInvitesQueryKey(),
    queryFn: async () => {
      const response = await invitesApi.invitesGet();
      return response.data.data;
    },
  });
}
