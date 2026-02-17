import { useSendInvite } from '@/entities/invites/api/useSendInvite';
import { Dialog, Input } from '@/shared/ui';
import { DialogContent } from '@radix-ui/react-dialog';
import { useForm } from 'react-hook-form';

interface InviteUserDialogProps {
  projectId: string;
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

interface IFormInput {
  email: string;
}

export const InviteUserDialog = (props: InviteUserDialogProps) => {
  const {
    mutate: inviteUser,
    isPending,
    error,
  } = useSendInvite(props.projectId);
  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<IFormInput>();

  const onSubmit = (formData: IFormInput) => {
    inviteUser(formData.email, {
      onSuccess: () => {
        props.setIsOpen(false);
      },
    });
  };

  return (
    <Dialog open={props.isOpen} onOpenChange={props.setIsOpen}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-4">
          <div className="flex flex-col gap-1">
            <Input
              type="email"
              placeholder="user@example.com"
              className="border rounded px-3 py-2"
              {...register('email', {
                required: 'Email обязателен',
                pattern: {
                  value: /^\S+@\S+\.\S+$/,
                  message: 'Некорректный email',
                },
              })}
              disabled={isPending}
            />

            {errors.email && (
              <span className="text-sm text-red-500">
                {errors.email.message}
              </span>
            )}
          </div>

          {error && (
            <span className="text-sm text-red-500">
              Не удалось отправить приглашение
            </span>
          )}

          <button
            type="submit"
            disabled={isPending}
            className="rounded bg-black px-4 py-2 text-white disabled:opacity-50"
          >
            {isPending ? 'Отправка...' : 'Пригласить'}
          </button>
        </form>
      </DialogContent>
    </Dialog>
  );
};
