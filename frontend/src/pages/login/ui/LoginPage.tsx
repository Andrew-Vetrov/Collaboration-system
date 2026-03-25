import { ButtonSocial } from '@/features/auth-by-google/';
import { ModeToggle } from '@/features/theme-toggle';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/shared/ui/';

export const LoginPage = () => {
  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8080/login-start';
  };

  return (
    <>
      <header className=" border-b absolute px-4 py-3 w-full">
        <div className="flex justify-end mx-auto max-w-5xl">
          <ModeToggle />
        </div>
      </header>
      <div className="flex items-center justify-center min-h-screen">
        <Card className="w-full max-w-sm p-4">
          <CardHeader>
            <CardTitle className="text-center text-3xl font-semibold">
              Авторизация
            </CardTitle>
            <CardDescription className="m-4 text-center">
              Для продолжения необходимо авторизоваться
            </CardDescription>
          </CardHeader>

          <CardContent>
            <ButtonSocial
              onClick={handleAuthClick}
              variant="outline"
              className="rounded-md"
            />
          </CardContent>
        </Card>
      </div>
    </>
  );
};
