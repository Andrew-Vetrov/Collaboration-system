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

interface CreateProjectDialogProps {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

export const CreateProjectDialog = (
  props: CreateProjectDialogProps
): JSX.Element => {
  const { mutate, isPending, isError, error } = useProjectCreate();

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    console.log('sadf');
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const name = formData.get('name')?.toString().trim() ?? '';
    const description = formData.get('description')?.toString().trim() ?? '';
    if (!name) {
      alert('Имя проекта обязательно');
      return;
    }
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
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>Создать новый проект</DialogTitle>
            <DialogDescription className="mb-2">
              Заполните данные проекта и нажмите «Создать проект».
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4">
            <div className="grid gap-3">
              <Label htmlFor="name-1">Имя проекта</Label>
              <Input id="name-1" name="name" />
            </div>
            <div className="grid gap-3">
              <Label htmlFor="description-1">Описание проекта</Label>
              <Textarea
                id="description-1"
                name="description"
                className="max-h-40 overflow-auto"
              />
            </div>
          </div>
          <DialogFooter className="flex flex-row justify-center">
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
