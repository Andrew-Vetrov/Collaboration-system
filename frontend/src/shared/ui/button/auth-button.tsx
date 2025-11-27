import { Button } from '@/shared/ui/button/button';
import type { ButtonProps } from '@/shared/ui/button/button';
import React from 'react';

const ButtonSocial = (props: ButtonProps) => {
  // return (
  //   <div className="flex w-full max-w-56 flex-col justify-center gap-4">
  //     <Button variant="outline" className="!border-[#e84133] !text-[#e84133]">
  //       <img
  //         src="https://cdn.shadcnstudio.com/ss-assets/brand-logo/google-icon.png?width=20&height=20&format=auto"
  //         alt="Google Icon"
  //         className="size-5"
  //       />
  //       <span className="flex flex-1 justify-center">Continue with Google</span>
  //     </Button>
  //   </div>
  // );
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
