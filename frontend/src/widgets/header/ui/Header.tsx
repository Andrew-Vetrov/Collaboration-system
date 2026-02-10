import type { JSX } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { Button } from '@/shared/ui';

import { handleLogout } from '@/features/auth-by-google/';
import { ModeToggle } from '@/features/theme-toggle';
import { MainUserMenu } from '@/entities/main-user';
import { routes } from '@/shared/route';
import { useProjectPermissions } from '@/entities/project/api/useProjectPermissions';

export const Header = (): JSX.Element => {
  const navigate = useNavigate();
  const location = useLocation();

  const hideBackOn = [routes.projectsRoute()];

  const showBackButton = !hideBackOn.includes(location.pathname);

  const handleBack = () => {
    if (location.key !== 'default') {
      navigate(-1);
    } else {
      navigate(routes.projectsRoute(), { replace: true });
    }
  };

  const { projectId } = useParams<{ projectId?: string }>();
  const { data: permissions } = useProjectPermissions(projectId);

  return (
    <header className="sticky top-0 z-50 border-b bg-background/95 backdrop-blur px-4 py-3">
      <div className="flex items-center justify-between max-w-7xl mx-auto">
        <div className="flex items-center">
          {showBackButton && (
            <Button variant="ghost" size="icon" onClick={handleBack}>
              <ArrowLeft className="h-6 w-6" />
            </Button>
          )}
        </div>

        {permissions && (
          <div className="text-sm text-muted-foreground absolute left-1/2 -translate-x-1/2">
            Осталось реакций: {permissions.likes_remain}
          </div>
        )}

        <div className="flex items-center gap-6 md:gap-8">
          <ModeToggle />
          <MainUserMenu handleLogout={() => handleLogout(navigate)} />
        </div>
      </div>
    </header>
  );
};
