import type { JSX } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, LogOut } from 'lucide-react';
import { Avatar, AvatarFallback } from '@/shared/ui/avatar';
import { Button } from '@/shared/ui';
import {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
} from '@/shared/ui';
import { handleLogout } from '@/features/auth-by-google/';

export const Header = (): JSX.Element => {
  const navigate = useNavigate();
  return (
    <header className="sticky top-0 z-50 flex items-center justify-between border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 px-4 py-3">
      <Button variant="ghost" size="icon" onClick={() => navigate(-1)}>
        <ArrowLeft className="h-6 w-6" />
      </Button>

      <DropdownMenu>
        <DropdownMenuTrigger asChild>
          <Button variant="ghost" className="relative h-8 w-8 rounded-full p-0">
            <Avatar className="h-9 w-9">
              <AvatarFallback className="bg-black text-white font-medium hover:bg-zinc-800 transition-colors">
                T
              </AvatarFallback>
            </Avatar>
          </Button>
        </DropdownMenuTrigger>
        <DropdownMenuContent>
          <DropdownMenuItem onClick={() => handleLogout(navigate)}>
            <LogOut className="mr-2 h-4 w-4" />
            <span>Выйти</span>
          </DropdownMenuItem>
        </DropdownMenuContent>
      </DropdownMenu>
    </header>
  );
};
