import { Button, Input, Textarea } from '@/shared/ui';
import { Label } from '@radix-ui/react-label';
import type {
  Control,
  FormState,
  UseFormHandleSubmit,
  UseFormRegister,
} from 'react-hook-form';

export interface IFormInput {
  name: string;
  description: string;
}

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
    <form
      onSubmit={handleSubmit(handlePublish)}
      className="flex flex-col gap-6 p-6 "
    >
      <div className="flex flex-col">
        <Label htmlFor="name-id" className="mb-1 font-semibold">
          Имя предложения
        </Label>
        <Input
          {...register('name', {
            required: 'Необходимо задать имя предложению',
            minLength: { value: 1, message: 'Имя не может быть пустым' },
            maxLength: { value: 30, message: 'Максимум 30 символов' },
            onBlur: handleBlur,
          })}
          id="name-id"
          className={`border rounded-lg px-3 py-2 ${
            errors.name ? 'border-red-500' : 'border'
          }`}
        />
        {errors.name && (
          <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>
        )}
      </div>

      <div className="grid">
        <Label htmlFor="description-id" className="mb-1 font-semibold">
          Описание предложения
        </Label>
        <Textarea
          {...register('description', {
            required: 'Необходимо задать описание предложению',
            minLength: { value: 1, message: 'Описание не может быть пустым' },
            onBlur: handleBlur,
          })}
          id="description-id"
          className={`border rounded-lg px-3 py-2 min-h-[120px] resize-none max-h-40 overflow-auto${
            errors.description ? 'border-red-500' : 'border'
          }`}
        />
        {errors.description && (
          <p className="text-red-500 text-sm mt-1">
            {errors.description.message}
          </p>
        )}
      </div>

      <div className="flex flex-col sm:flex-row gap-3 justify-end">
        <Button
          type="submit"
          className="bg-blue-600 text-white hover:bg-blue-700"
        >
          Опубликовать
        </Button>
        <Button
          type="button"
          onClick={handleDelete}
          className="bg-red-500 text-white hover:bg-red-600"
        >
          Удалить черновик
        </Button>
      </div>
    </form>
  );
}
