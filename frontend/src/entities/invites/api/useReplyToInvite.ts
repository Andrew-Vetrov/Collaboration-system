import { invitesApi } from '@/shared/api';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { getInvitesQueryKey } from '../lib/getInviteQueryKey';
import type { Invite } from '../model/types';
import { getProjectInvitesQueryKey } from '../lib/getProjectInvitesQueryKey';

interface ReplyToInviteVariables {
  inviteId: string;
  inviteResponse: boolean;
  projectId?: string;
}

export function useReplyToInvite() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({
      inviteId,
      inviteResponse,
    }: ReplyToInviteVariables) => {
      return invitesApi.invitesInviteIdDelete(inviteId, inviteResponse);
    },
    onMutate: async ({ inviteId }) => {
      await queryClient.cancelQueries({ queryKey: getInvitesQueryKey() });

      const previousInvitesData =
        queryClient.getQueryData<Invite[]>(getInvitesQueryKey()) ?? [];
      const newInviteList = previousInvitesData.filter(
        invite => invite.invite_id != inviteId
      );

      queryClient.setQueryData<Invite[]>(getInvitesQueryKey(), newInviteList);

      return { previusInvitesData: previousInvitesData };
    },
    onError: (error, vars, context) => {
      if (!context) return;

      queryClient.setQueryData<Invite[]>(
        getInvitesQueryKey(),
        context.previusInvitesData
      );
    },
    onSettled: (data, error, variables) => {
      queryClient.invalidateQueries({ queryKey: getInvitesQueryKey() });
      if (variables.projectId) {
        queryClient.invalidateQueries({
          queryKey: getProjectInvitesQueryKey(variables.projectId),
        });
      }
    },
  });
}
