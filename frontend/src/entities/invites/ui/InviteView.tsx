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
    <div
      className={cn(
        'flex flex-col gap-3 w-full p-3 sm:flex-row sm:items-center sm:gap-2',
        className
      )}
    >
      <div className="flex-1 min-w-0 text-sm">
        <div className="text-foreground">
          Пользователь{' '}
          <span className="font-semibold wrap-break-word">
            {invite.sender_nickname}
          </span>
        </div>

        <div className="text-muted-foreground">
          приглашает в проект{' '}
          <span className="font-medium break-all">{invite.project_name}</span>
        </div>
      </div>

      <div className="flex gap-2 sm:gap-1 self-end sm:self-auto">
        <Button
          variant="outline"
          size="icon"
          className="h-9 w-9"
          onClick={() =>
            replyMutation({
              inviteId: invite.invite_id,
              inviteResponse: true,
            })
          }
        >
          <Plus className="h-4 w-4" />
        </Button>

        <Button
          variant="outline"
          size="icon"
          className="h-9 w-9"
          onClick={() =>
            replyMutation({
              inviteId: invite.invite_id,
              inviteResponse: false,
            })
          }
        >
          <Minus className="h-4 w-4" />
        </Button>
      </div>
    </div>
  );
}
