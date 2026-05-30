import { useCreateProjectTag } from '@/entities/tag/api/useCreateProjectTag';
import {
  Button,
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
  Input,
  Label,
} from '@/shared/ui';
import { TooltipProvider } from '@/shared/ui/tooltip';
import type { Dispatch, SetStateAction } from 'react';
import { useForm } from 'react-hook-form';

interface TagCreationDialogProps {
  open: boolean;
  setIsOpen: Dispatch<SetStateAction<boolean>>;
  projectId: string;
}

interface CreateTagFormInput {
  tagName: string;
  tagColor: string;
}

export const TagCreationDialog = ({
  open,
  projectId,
  setIsOpen,
}: TagCreationDialogProps) => {
  const {
    handleSubmit,
    register,
    formState: { errors },
  } = useForm<CreateTagFormInput>();

  const {
    mutate: createTag,
    isPending,
    isError,
  } = useCreateProjectTag(projectId);
  const onSubmit = ({ tagColor, tagName }: CreateTagFormInput) => {
    createTag(
      {
        name: tagName,
        color: tagColor,
      },
      {
        onSuccess: () => {
          setIsOpen(false);
        },
      }
    );
  };
  return (
    <TooltipProvider>
      <Dialog open={open} onOpenChange={setIsOpen}>
        <DialogContent>
          <DialogTitle>Создание тега</DialogTitle>
          <DialogDescription>Здесь можно создать новый тег</DialogDescription>

          <form
            onSubmit={handleSubmit(onSubmit)}
            className="flex flex-col gap-3"
          >
            <div className="flex flex-col gap-2">
              <Label htmlFor="tagName">Задайте имя тега:</Label>
              <Input
                id="tagName"
                placeholder="Имя тега"
                {...register('tagName', {
                  minLength: 1,
                  maxLength: 20,
                  required: 'Необходимо ввести имя',
                  pattern: {
                    value: /^[a-zA-Zа-яА-ЯёЁ0-9]+$/,
                    message: 'Некорректное имя',
                  },
                })}
              />
              {errors.tagName && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.tagName.message}
                </p>
              )}
            </div>
            <div className="flex gap-2">
              <Label htmlFor="tagColor">Задайте цвет бэйджика тега:</Label>
              <Input
                id="tagColor"
                {...register('tagColor', {
                  required: 'Необходимо задать цвет',
                })}
                type="color"
                className="w-13 h-10"
              />
              {errors.tagColor && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.tagColor.message}
                </p>
              )}
            </div>
            <Button type="submit" disabled={isPending}>
              Добавить
            </Button>
            {isError && (
              <p className="text-red-500 text-sm mt-1">
                Не удалось создать тег
              </p>
            )}
          </form>
        </DialogContent>
      </Dialog>
    </TooltipProvider>
  );
};
