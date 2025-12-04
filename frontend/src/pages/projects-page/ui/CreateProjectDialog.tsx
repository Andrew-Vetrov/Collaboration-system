import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  DialogClose,
} from '@/shared/ui';
import { Input, Label, Textarea, Button } from '@/shared/ui';
import { useProjectCreate } from '../api/useProjectCreate';
import { useForm } from 'react-hook-form';

interface CreateProjectDialogProps {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

interface IFormInput {
  name: string;
  description: string;
}

export const CreateProjectDialog = (
  props: CreateProjectDialogProps
): JSX.Element => {
  const { mutate, isPending, isError, error } = useProjectCreate();

  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<IFormInput>();

  const onSubmit = (data: IFormInput) => {
    const name = data.name;
    const description = data.description;
    mutate(
      { name, description },
      {
        onSuccess: () => {
          props.setIsOpen(false);
        },
        onError: err => {
          console.error('Ошибка создания проекта:', err);
        },
      }
    );
  };

  return (
    <Dialog open={props.isOpen} onOpenChange={props.setIsOpen}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)}>
          <DialogHeader>
            <DialogTitle>Создать новый проект</DialogTitle>
            <DialogDescription className="mb-2">
              Заполните данные проекта и нажмите «Создать проект».
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4">
            <div className="grid gap-3">
              <Label htmlFor="name-1">Имя проекта</Label>
              <Input
                {...register('name', {
                  required: 'Необходимо написать имя проекта',
                  maxLength: 30,
                })}
                id="name-1"
                name="name"
              />
              <p className="text-red-600">{errors.name?.message}</p>
            </div>
            <div className="grid gap-3">
              <Label htmlFor="description-1">Описание проекта</Label>
              <Textarea
                {...register('description', {
                  required: 'Необходимо написать описание для проекта',
                })}
                id="description-1"
                name="description"
                className="max-h-40 overflow-auto"
              />
              <p className="text-red-600">{errors.description?.message}</p>
            </div>
          </div>
          <DialogFooter className="flex flex-row justify-center mt-4">
            <DialogClose asChild>
              <Button variant="outline">Отменить</Button>
            </DialogClose>
            <Button
              variant="outline"
              type="submit"
              className="bg-black"
              disabled={isPending}
            >
              {isPending ? 'Создание...' : 'Создать проект'}
            </Button>
          </DialogFooter>
          {isError && (
            <p className="text-red-500 mt-2">Ошибка: {String(error)}</p>
          )}
        </form>
      </DialogContent>
    </Dialog>
  );
};
