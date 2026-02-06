import { Button, Input, Textarea } from '@/shared/ui';
import { Label } from '@radix-ui/react-label';
import type {
  Control,
  FormState,
  UseFormHandleSubmit,
  UseFormRegister,
} from 'react-hook-form';
import type { IFormInput } from '../model/types';

export interface SuggestionEditingFieldsProps {
  register: UseFormRegister<IFormInput>;
  handleSubmit: UseFormHandleSubmit<IFormInput>;
  control: Control<IFormInput>;
  formState: FormState<IFormInput>;
  handlePublish: (data: IFormInput) => void;
  handleDelete: () => void;
  handleBlur: () => void;
}

export function SuggestionEditingFields({
  register,
  handleSubmit,
  formState: { errors },
  handlePublish,
  handleDelete,
  handleBlur,
}: SuggestionEditingFieldsProps) {
  return (
    <form onSubmit={handleSubmit(handlePublish)}>
      <Label htmlFor="name-id">Имя предложения</Label>
      <Input
        {...register('name', {
          required: 'Необходимо задать имя предложению',
          minLength: { value: 1, message: 'Имя не может быть пустым' },
          maxLength: { value: 30, message: 'Максимум 30 символов' },
          onBlur: handleBlur,
        })}
        id="name-id"
      />
      {errors.name && (
        <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>
      )}

      <Label htmlFor="description-id">Описание предложения</Label>
      <Textarea
        {...register('description', {
          required: 'Необходимо задать описание предложению',
          minLength: { value: 1, message: 'Описание не может быть пустым' },
          onBlur: handleBlur,
        })}
        id="description-id"
      />
      {errors.description && (
        <p className="text-red-500 text-sm mt-1">
          {errors.description.message}
        </p>
      )}

      <Button type="submit">Опубликовать</Button>
      <Button type="reset" onClick={handleDelete}>
        Удалить черновик
      </Button>
    </form>
  );
}
