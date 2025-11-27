import { Button } from '@/shared/ui/button/button';
import type { ButtonProps } from '@/shared/ui/button/button';

const ButtonSocial = (props: ButtonProps) => {
  return (
    <div className="flex w-full flex-col justify-center gap-4">
      <Button {...props} className="px-6 mx-auto">
        <img
          src="https://cdn.shadcnstudio.com/ss-assets/brand-logo/google-icon.png?width=20&height=20&format=auto"
          alt="Google Icon"
          className="size-6"
        />
        <span className="justify-center">Авторизация через Google</span>
      </Button>
    </div>
  );
};

export default ButtonSocial;
