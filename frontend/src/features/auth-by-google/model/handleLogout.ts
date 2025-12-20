import type { NavigateFunction } from 'react-router-dom';

export const handleLogout = (navigate: NavigateFunction): void => {
  if (!localStorage.getItem('jwt')) {
    return;
  }
  localStorage.removeItem('jwt');
  navigate('/auth', { replace: true });
};
