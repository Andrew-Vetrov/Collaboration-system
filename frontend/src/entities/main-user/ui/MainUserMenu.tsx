import {
  Avatar,
  AvatarFallback,
  Button,
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from '@/shared/ui';
import { useAuthMe } from '../api/useAuthMe';
import { LogOut } from 'lucide-react';

interface MainUserMenuProps {
  handleLogout: () => void;
}

export function MainUserMenu({ handleLogout }: MainUserMenuProps) {
  const { data: user } = useAuthMe();
  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative h-8 w-8 rounded-full p-0">
          <Avatar className="h-9 w-9">
            <AvatarFallback className="bg-black text-white font-medium hover:bg-zinc-800 transition-colors">
              <img
                src={user?.avatar_url}
                alt="User avatar"
                className="w-full h-full object-cover"
              />
            </AvatarFallback>
          </Avatar>{' '}
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent>
        <DropdownMenuItem>
          <span className="block max-w-[150px] truncate">{user?.nickname}</span>
        </DropdownMenuItem>
        <DropdownMenuItem>
          <span>{user?.email}</span>
        </DropdownMenuItem>
        <DropdownMenuItem onClick={() => handleLogout()}>
          <LogOut className="mr-2 h-4 w-4" />
          <span>Выйти</span>
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  );
}
