import { ButtonSocial } from '@/features/auth-by-google/';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/shared/ui/';

export const LoginPage = () => {
  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8080/auth';
  };

  return (
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
  );
};
