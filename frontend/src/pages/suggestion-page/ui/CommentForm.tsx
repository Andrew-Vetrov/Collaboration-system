import { Button, Textarea } from '@/shared/ui';
import { useForm, type RegisterOptions } from 'react-hook-form';
import { useCommentCreate } from '@/entities/comment/api/useCommentCreate';

interface CommentFormProps {
  suggestionId: string;
  formOptions?: RegisterOptions<IFormInput, 'text'> | undefined;
}

interface IFormInput {
  text: string;
}

export function CommentForm({ formOptions, suggestionId }: CommentFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<IFormInput>();

  const { mutate, isPending, isError } = useCommentCreate(suggestionId);
  return (
    <form onSubmit={handleSubmit(data => mutate(data))} className="flex  gap-4">
      <div className="grid w-full gap-2">
        <Textarea
          className="resize-none max-h-40 overflow-auto"
          placeholder="Оставьте комментарий..."
          {...register('text', formOptions)}
        />
        {errors.text && (
          <p className="text-red-500 text-sm">{errors.text.message}</p>
        )}
      </div>
      <Button
        disabled={isPending}
        className={isError ? 'border-red-500' : ''}
        type="submit"
      >
        Отправить
      </Button>
    </form>
  );
}
