import {
  DropdownMenu,
  DropdownMenuItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
} from '@/shared/ui';
import { useInvites } from '../api/useInvites';
import { InviteView } from './InviteView';

export function InvitesMenu() {
  const { data: invites } = useInvites();

  return (
    <DropdownMenu>
      <DropdownMenuTrigger className="">Приглашения</DropdownMenuTrigger>

      <DropdownMenuContent
        side="bottom"
        align="end"
        sideOffset={4}
        className="w-72 sm:w-90"
      >
        {!invites ? (
          <div className="px-4 py-2">Загрузка...</div>
        ) : invites.length === 0 ? (
          <div className="px-4 py-2">Нет приглашений</div>
        ) : (
          invites.map(invite => (
            <DropdownMenuItem key={invite.invite_id} className="p-0">
              <InviteView invite={invite} />
            </DropdownMenuItem>
          ))
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
