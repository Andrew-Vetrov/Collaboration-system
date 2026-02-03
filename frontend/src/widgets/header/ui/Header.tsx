import type { JSX } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/shared/ui';

import { handleLogout } from '@/features/auth-by-google/';
import { ModeToggle } from '@/features/theme-toggle';
import { MainUserMenu } from '@/entities/main-user';

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

      <MainUserMenu handleLogout={() => handleLogout(navigate)} />
    </header>
  );
};
