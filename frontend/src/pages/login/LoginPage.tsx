import { ButtonSocial } from '@/shared/ui/button/';

export const LoginPage = () => {
  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8000/auth';
  };

  return (
    <div className="flex flex-col items-center justify-center min-h-screen gap-4">
      <h1 className="text-3xl font-semibold">Авторизация</h1>
      <ButtonSocial onClick={handleAuthClick} variant="destructive">
        Войти
      </ButtonSocial>
    </div>
  );
};
