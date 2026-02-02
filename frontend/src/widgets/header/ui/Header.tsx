import type { JSX } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
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
import { ModeToggle } from '@/features/theme-toggle';

export const Header = (): JSX.Element => {
  const navigate = useNavigate();
  const location = useLocation();

  const hideBackOn = ['/'];

  const showBackButton = !hideBackOn.includes(location.pathname);

  const handleBack = () => {
    if (location.key !== 'default') {
      navigate(-1);
    } else {
      navigate('/', { replace: true });
    }
  };

  return (
    <header className="sticky top-0 z-50 flex items-center justify-between border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60 px-4 py-3">
      {showBackButton && (
        <Button variant="ghost" size="icon" onClick={handleBack}>
          <ArrowLeft className="h-6 w-6" />
        </Button>
      )}

      <ModeToggle />

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
