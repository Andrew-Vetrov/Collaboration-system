import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { handleGoogleCallback } from '../lib/handleGoogleCallback';

export const AuthSuccess = () => {
  const navigate = useNavigate();
  const { search } = useLocation();

  useEffect(() => {
    handleGoogleCallback(search, navigate);
  }, [search, navigate]);

  return <h2>Авторизация успешна! Перенаправляем...</h2>;
};