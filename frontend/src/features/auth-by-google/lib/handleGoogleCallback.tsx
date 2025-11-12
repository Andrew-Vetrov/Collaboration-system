import { NavigateFunction } from 'react-router-dom';

export const handleGoogleCallback = (
  search: string,
  navigate: NavigateFunction
) => {
  const params = new URLSearchParams(search);
  const token = params.get('token');

  if (token) {
    localStorage.setItem('jwt', token);
    navigate('/projects', { replace: true });
  } else {
    navigate('/', { replace: true });
  }
};