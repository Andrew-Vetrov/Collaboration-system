import ButtonSocial from './auth-button';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from './card';

export const LoginPage = () => {
  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8000/auth';
  };

  return (
    <div className="flex items-center justify-center min-h-screen">
      <Card className="w-full max-w-sm p-8">
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
            className="w-full rounded-md"
          />
        </CardContent>
      </Card>
    </div>
  );
};
