import { useCreateProjectRole } from '@/entities/role';
import type { ProjectsProjectIdRolesPostRequest } from '@/shared/api/generated';
import {
  Button,
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
  Input,
  Label,
} from '@/shared/ui';
import {
  Tooltip,
  TooltipContent,
  TooltipProvider,
  TooltipTrigger,
} from '@/shared/ui/tooltip';
import { Info } from 'lucide-react';
import { type Dispatch, type SetStateAction } from 'react';
import { useForm } from 'react-hook-form';

interface RoleCreationDialogProps {
  open: boolean;
  setIsOpen: Dispatch<SetStateAction<boolean>>;
  projectId: string;
}

interface CreateRoleFormInput {
  roleName: string;
  roleColor: string;
  roleVotesCount: number;
}

export const RoleCreationDialog = ({
  open,
  setIsOpen,
  projectId,
}: RoleCreationDialogProps) => {
  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<CreateRoleFormInput>();

  const {
    mutate: createRole,
    isPending,
    isError,
  } = useCreateProjectRole(projectId);

  const onSubmit = ({
    roleColor,
    roleName,
    roleVotesCount,
  }: CreateRoleFormInput) => {
    console.log('submit');
    createRole({
      name: roleName,
      color: roleColor,
      likes_amount: roleVotesCount,
    });
  };
  return (
    <TooltipProvider>
      <Dialog open={open} onOpenChange={setIsOpen}>
        <Dialog open={open} onOpenChange={setIsOpen}>
          <DialogContent>
            <DialogTitle>Создание роли</DialogTitle>
            <DialogDescription>
              Здесь можно создать новую роль
            </DialogDescription>

            <form
              onSubmit={handleSubmit(onSubmit)}
              className="flex flex-col gap-3"
            >
              <div className="flex flex-col gap-2">
                <Label htmlFor="roleName">Задайте имя роли:</Label>
                <Input
                  id="roleName"
                  placeholder="Имя роли"
                  {...register('roleName', {
                    minLength: 1,
                    maxLength: 20,
                    required: 'Необходимо ввести имя',
                    pattern: {
                      value: /^[a-zA-Zа-яА-ЯёЁ0-9]+$/,
                      message: 'Некорректное имя',
                    },
                  })}
                />
                {errors.roleName && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.roleName.message}
                  </p>
                )}
              </div>
              <div className="flex gap-2">
                <Label htmlFor="roleColor">Задайте цвет бэйджика роли:</Label>
                <Input
                  id="roleColor"
                  {...register('roleColor', {
                    required: 'Необходимо задать цвет',
                  })}
                  type="color"
                  className="w-13 h-10"
                />
                {errors.roleColor && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.roleColor.message}
                  </p>
                )}
              </div>
              <div className="flex flex-col gap-2">
                <div className="flex gap-2">
                  <Label htmlFor="roleVotesCount">Количество голосов:</Label>
                  <Tooltip>
                    <TooltipTrigger asChild>
                      <button type="button">
                        <Info />
                      </button>
                    </TooltipTrigger>
                    <TooltipContent className="max-w-40">
                      <p>
                        Это поле определеяет какое количество голосов на период
                        голосования будет выдано человеку с данной ролью. Если у
                        человека несколько ролей, то выбирается наибольшее число
                        из них.
                      </p>
                    </TooltipContent>
                  </Tooltip>
                </div>
                <Input
                  id="roleVotesCount"
                  type="number"
                  {...register('roleVotesCount', {
                    min: { value: 1, message: 'Минимум 1' },
                    max: { value: 999, message: 'Максимум 999' },
                    valueAsNumber: true,
                    required: 'Необходимо задать количество реакций',
                  })}
                />
                {errors.roleVotesCount && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.roleVotesCount.message}
                  </p>
                )}
              </div>
              <Button type="submit" disabled={isPending}>
                Добавить
              </Button>
              {isError && (
                <p className="text-red-500 text-sm mt-1">
                  Не удалось создать роль
                </p>
              )}
            </form>
          </DialogContent>
        </Dialog>
      </Dialog>
    </TooltipProvider>
  );
};
