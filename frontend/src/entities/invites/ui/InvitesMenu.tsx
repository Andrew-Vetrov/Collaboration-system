import {
  DropdownMenu,
  DropdownMenuItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuPortal,
} from '@/shared/ui';
import { useInvites } from '../api/useInvites';
import { InviteView } from './InviteView';

interface InvitesMenuProps {
  isSubMenu?: boolean;
}

export function InvitesMenu({ isSubMenu = false }: InvitesMenuProps) {
  const { data: invites } = useInvites();

  if (isSubMenu) {
    return (
      <DropdownMenuSub>
        <DropdownMenuSubTrigger>Приглашения</DropdownMenuSubTrigger>
        <DropdownMenuPortal>
          <DropdownMenuSubContent>
            {!invites ? (
              <div className="px-4 py-2">Загрузка...</div>
            ) : invites.length === 0 ? (
              <div className="px-4 py-2">Нет приглашений</div>
            ) : (
              invites.map(invite => (
                <DropdownMenuItem key={invite.invite_id}>
                  <InviteView invite={invite} />
                </DropdownMenuItem>
              ))
            )}
          </DropdownMenuSubContent>
        </DropdownMenuPortal>
      </DropdownMenuSub>
    );
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger>
        <div>Приглашения</div>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        {!invites ? (
          <div className="px-4 py-2">Загрузка...</div>
        ) : invites.length === 0 ? (
          <div className="px-4 py-2">Нет приглашений</div>
        ) : (
          invites.map(invite => (
            <DropdownMenuItem key={invite.invite_id}>
              <InviteView invite={invite} />
            </DropdownMenuItem>
          ))
        )}
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
