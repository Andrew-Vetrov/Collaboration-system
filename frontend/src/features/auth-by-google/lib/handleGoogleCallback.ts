import { routes } from '@/shared/route';
import type { NavigateFunction } from 'react-router-dom';

export const handleGoogleCallback = (
  search: string,
  navigate: NavigateFunction
) => {
  const params = new URLSearchParams(search);
  const token = params.get('token');

  if (token) {
    localStorage.setItem('jwt', token);

    navigate(routes.projectsRoute(), { replace: true });
  } else {
    navigate(routes.authRoute(), { replace: true });
  }
};
