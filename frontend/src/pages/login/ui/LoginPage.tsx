import { ButtonSocial } from '@/features/auth-by-google/';
import { ModeToggle } from '@/features/theme-toggle';
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
  Dialog,
  DialogContent,
  Input,
} from '@/shared/ui/';
import { Checkbox } from '@/shared/ui/checkbox';
import { useState } from 'react';
import { PrivacyPolicy } from './PrivacyPolicy';
import { UserAgent } from './UserAgent';

export const LoginPage = () => {
  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8080/login-start';
  };

  const [flag, setFlag] = useState<boolean>(false);

  const [isUserAgentOpen, setIsUserAgentOpen] = useState<boolean>(false);
  const [isPrivacyPolicy, setPrivacyPolicy] = useState<boolean>(false);

  return (
    <>
      <header className=" border-b absolute px-4 py-3 w-full">
        <div className="flex justify-end mx-auto max-w-5xl">
          <ModeToggle />
        </div>
      </header>
      <div className="flex items-center flex-col gap-2 justify-center min-h-screen">
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
              disabled={!flag}
            />
          </CardContent>
        </Card>
        <div className="w-full max-w-sm flex gap-4">
          <Checkbox
            checked={flag}
            onCheckedChange={() => setFlag(prev => !prev)}
            className="rounded-md w-9 h-9 border-primary"
          />
          <span>
            Нажимая «Авторизация через Google», вы принимаете условия{' '}
            <b
              className="cursor-pointer"
              onClick={() => {
                setIsUserAgentOpen(true);
              }}
            >
              Пользовательского соглашения
            </b>{' '}
            и даете согласие на обработку своих персональных данных в
            соответствии с{' '}
            <b
              className="cursor-pointer"
              onClick={() => {
                setPrivacyPolicy(true);
              }}
            >
              Политикой конфиденциальности
            </b>
          </span>
        </div>
      </div>
      <Dialog open={isUserAgentOpen} onOpenChange={setIsUserAgentOpen}>
        <DialogContent className="max-h-[70vh] overflow-y-scroll">
          <UserAgent />
        </DialogContent>
      </Dialog>
      <Dialog open={isPrivacyPolicy} onOpenChange={setPrivacyPolicy}>
        <DialogContent className="max-h-[70vh] overflow-y-scroll">
          <PrivacyPolicy />
        </DialogContent>
      </Dialog>
    </>
  );
};
