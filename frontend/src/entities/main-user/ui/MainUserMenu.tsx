import {
  Avatar,
  AvatarFallback,
  Button,
  DropdownMenu,
  DropdownMenuItem,
  DropdownMenuContent,
  DropdownMenuTrigger,
  DropdownMenuSub,
  DropdownMenuSubContent,
  DropdownMenuSubTrigger,
  DropdownMenuPortal,
} from '@/shared/ui';
import { useAuthMe } from '../api/useAuthMe';
import { LogOut } from 'lucide-react';

interface MainUserMenuProps {
  isSubMenu?: boolean;
  handleLogout: () => void;
}

export function MainUserMenu({
  handleLogout,
  isSubMenu = false,
}: MainUserMenuProps) {
  const { data: user } = useAuthMe();

  if (isSubMenu) {
    return (
      <DropdownMenuSub>
        <DropdownMenuSubTrigger>
          <div className="flex items-center gap-2 cursor-pointer">
            <Avatar className="h-6 w-6">
              <AvatarFallback>
                <img
                  src={user?.avatar_url}
                  alt="User avatar"
                  className="w-full h-full object-cover"
                />
              </AvatarFallback>
            </Avatar>
            <span className="truncate">{user?.nickname}</span>
          </div>
        </DropdownMenuSubTrigger>

        <DropdownMenuPortal>
          <DropdownMenuSubContent>
            <DropdownMenuItem className="pointer-events-none">
              <span className="block max-w-[150px] truncate">
                {user?.nickname}
              </span>
            </DropdownMenuItem>
            <DropdownMenuItem className="pointer-events-none">
              <span>{user?.email}</span>
            </DropdownMenuItem>
            <DropdownMenuItem onClick={handleLogout} className="cursor-pointer">
              <LogOut className="mr-2 h-4 w-4" />
              <span>Выйти</span>
            </DropdownMenuItem>
          </DropdownMenuSubContent>
        </DropdownMenuPortal>
      </DropdownMenuSub>
    );
  }

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button
          variant="ghost"
          className="relative h-8 w-8 rounded-full p-0 cursor-pointer"
        >
          <Avatar className="h-9 w-9">
            <AvatarFallback className="bg-black text-white font-medium hover:bg-zinc-800 transition-colors">
              <img
                src={user?.avatar_url}
                alt="User avatar"
                className="w-full h-full object-cover"
              />
            </AvatarFallback>
          </Avatar>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuItem className="pointer-events-none">
          <span className="block max-w-[150px] truncate">{user?.nickname}</span>
        </DropdownMenuItem>
        <DropdownMenuItem className="pointer-events-none">
          <span>{user?.email}</span>
        </DropdownMenuItem>
        <DropdownMenuItem onClick={handleLogout} className="cursor-pointer">
          <LogOut className="mr-2 h-4 w-4" />
          <span>Выйти</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
