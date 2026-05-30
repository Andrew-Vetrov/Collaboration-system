import { useUpdateProjectTag } from '@/entities/tag/api/useUpdateProjectTag';
import type { Tag } from '@/entities/tag/model/types';
import {
  Button,
  Dialog,
  DialogContent,
  DialogDescription,
  DialogTitle,
  Input,
  Label,
} from '@/shared/ui';
import type { Dispatch, SetStateAction } from 'react';
import { useEffect } from 'react';
import { useForm } from 'react-hook-form';

interface TagEditDialogProps {
  open: boolean;
  setIsOpen: Dispatch<SetStateAction<boolean>>;
  projectId: string;
  tag: Tag;
}

interface EditTagFormInput {
  tagName: string;
  tagColor: string;
}

export const TagEditDialog = ({
  open,
  projectId,
  setIsOpen,
  tag,
}: TagEditDialogProps) => {
  const {
    handleSubmit,
    register,
    reset,
    formState: { errors },
  } = useForm<EditTagFormInput>({
    defaultValues: {
      tagName: tag.name,
      tagColor: tag.color ?? '#000000',
    },
  });

  const {
    mutate: updateTag,
    isPending,
    isError,
  } = useUpdateProjectTag(projectId);

  useEffect(() => {
    reset({
      tagName: tag.name,
      tagColor: tag.color ?? '#000000',
    });
  }, [reset, tag]);

  const onSubmit = ({ tagColor, tagName }: EditTagFormInput) => {
    updateTag(
      {
        tagId: tag.tag_id,
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
    <Dialog open={open} onOpenChange={setIsOpen}>
      <DialogContent>
        <DialogTitle>Редактирование тега</DialogTitle>
        <DialogDescription>
          Измените название или цвет тега.
        </DialogDescription>
        <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-3">
          <div className="flex flex-col gap-2">
            <Label htmlFor={`tag-name-${tag.tag_id}`}>Имя тега</Label>
            <Input
              id={`tag-name-${tag.tag_id}`}
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
          <div className="flex items-center gap-2">
            <Label htmlFor={`tag-color-${tag.tag_id}`}>Цвет</Label>
            <Input
              id={`tag-color-${tag.tag_id}`}
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
            {isPending ? 'Сохранение...' : 'Сохранить'}
          </Button>
          {isError && (
            <p className="text-red-500 text-sm mt-1">
              Не удалось обновить тег
            </p>
          )}
        </form>
      </DialogContent>
    </Dialog>
  );
};
