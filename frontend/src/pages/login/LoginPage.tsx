import { ButtonSocial } from '@/shared/ui/button/';

export const LoginPage = () => {
  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8000/auth';
  };

  return (
    <div className="flex items-center justify-center min-h-screen gap-4">
      <div className="flex flex-col w-full max-w-sm justify-center border p-8 rounded-md bg-[oklch(0.2816_0.006_258.35)] shadow-md">
        <h1 className="text-3xl font-semibold text-center">Авторизация</h1>
        <p className="m-4 text-center">
          Для продолжения необходимо авторизоваться
        </p>
        <ButtonSocial
          onClick={handleAuthClick}
          variant="outline"
          className="rounded-md"
        />
      </div>
    </div>
  );
};
