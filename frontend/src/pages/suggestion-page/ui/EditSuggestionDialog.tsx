import { STATUS_LABELS } from '@/entities/suggestion/lib/status';
import { useSuggestionUpdate } from '@/features/suggestion-update/api/useSuggestionUpdate';
import type { SuggestionsSuggestionIdPutRequestStatusEnum } from '@/shared/api/generated';
import {
  Button,
  Dialog,
  DialogClose,
  DialogFooter,
  DialogHeader,
  Input,
  Label,
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
  Textarea,
  DialogContent,
  DialogTitle,
  DialogDescription,
} from '@/shared/ui';
import { Controller, useForm } from 'react-hook-form';

interface EditSuggestionDialogProps {
  isOpen: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
  projectId: string;
  suggestionId: string;
  isAdmin: boolean;
  name: string;
  description: string;
  status: SuggestionsSuggestionIdPutRequestStatusEnum;
}

interface IFormInput {
  name: string;
  description: string;
  status: SuggestionsSuggestionIdPutRequestStatusEnum;
}

export function EditSuggestionDialog(props: EditSuggestionDialogProps) {
  const {
    register,
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<IFormInput>({
    defaultValues: {
      name: props.name,
      description: props.description,
      status: props.status,
    },
  });

  const { mutate, isPending, isError, error } = useSuggestionUpdate(
    props.projectId,
    props.suggestionId
  );

  const onSubmit = (formData: IFormInput) => {
    mutate(formData, {
      onSuccess: () => {
        props.setIsOpen(false);
      },
    });
  };

  return (
    <Dialog open={props.isOpen} onOpenChange={props.setIsOpen}>
      <DialogContent>
        <form onSubmit={handleSubmit(onSubmit)}>
          <div className="flex flex-col gap-4">
            <DialogHeader>
              <DialogTitle>Редактирование предложения</DialogTitle>
              <DialogDescription>
                Измените название, описание или статус предложения. Изменения
                сохранятся после нажатия «Сохранить».
              </DialogDescription>
            </DialogHeader>
            <div className="grid gap-3">
              <Label htmlFor="name-id">Имя проекта</Label>
              <Input
                id="name-id"
                {...register('name', {
                  required: 'Необходимо задать имя предложению',
                  minLength: { value: 1, message: 'Имя не может быть пустым' },
                  maxLength: { value: 30, message: 'Максимум 30 символов' },
                })}
              />
              {errors.name && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.name.message}
                </p>
              )}
            </div>
            <div className="grid gap-3">
              <Label htmlFor="description-1">Описание проекта</Label>
              <Textarea
                {...register('description', {
                  required: 'Необходимо задать описание предложению',
                  minLength: {
                    value: 1,
                    message: 'Описание не может быть пустым',
                  },
                })}
                id="description-1"
                className="max-h-40 overflow-auto"
              />
              {errors.description && (
                <p className="text-red-500 text-sm mt-1">
                  {errors.description.message}
                </p>
              )}
            </div>
            {props.isAdmin && (
              <div className="grid gap-3">
                <Label htmlFor="status-id">Статус предложения</Label>
                <Controller
                  name="status"
                  control={control}
                  render={({ field }) => (
                    <Select
                      onValueChange={field.onChange}
                      defaultValue={field.value}
                    >
                      <SelectTrigger id="status-id">
                        <SelectValue placeholder="Выберите статус" />
                      </SelectTrigger>
                      <SelectContent>
                        {Object.entries(STATUS_LABELS)
                          .filter(([key]) => key !== 'draft')
                          .map(([key, value]) => (
                            <SelectItem key={key} value={key}>
                              {value}
                            </SelectItem>
                          ))}
                      </SelectContent>
                    </Select>
                  )}
                />
                {errors.status && (
                  <p className="text-red-500 text-sm mt-1">
                    {errors.status.message}
                  </p>
                )}
              </div>
            )}
          </div>
          <DialogFooter className="flex flex-row justify-center mt-4">
            <DialogClose asChild>
              <Button variant="outline">Отменить</Button>
            </DialogClose>
            <Button variant="outline" type="submit" disabled={isPending}>
              {isPending ? 'Сохранение...' : 'Сохранить'}
            </Button>
          </DialogFooter>
          {isError && (
            <p className="text-red-500 mt-2">Ошибка: {String(error)}</p>
          )}
        </form>
      </DialogContent>
    </Dialog>
  );
}
