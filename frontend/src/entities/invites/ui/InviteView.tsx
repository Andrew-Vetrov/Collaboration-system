import { cn } from '@/shared/lib/utils';
import type { Invite } from '../model/types';
import { Button } from '@/shared/ui';
import { useReplyToInvite } from '../api/useReplyToInvite';
import { Minus, Plus } from 'lucide-react';

interface InviteViewProps {
  invite: Invite;
  className?: string;
}

export function InviteView({ invite, className }: InviteViewProps) {
  const { mutate: replyMutation } = useReplyToInvite();
  return (
    <div className="flex">
      <div>руддщ</div>
      <div className={cn('flex items-center gap-1', className)}>
        <Button
          onClick={() => {
            replyMutation({ inviteId: invite.invite_id, inviteResponse: true });
          }}
          variant="outline"
          className="size-8"
        >
          <Plus className="size-8" />
        </Button>
        <Button
          onClick={() => {
            replyMutation({
              inviteId: invite.invite_id,
              inviteResponse: false,
            });
          }}
          variant="outline"
          className="size-8"
        >
          <Minus className="size-8" />
        </Button>
      </div>
    </div>
  );
}
