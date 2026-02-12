import { useCommentReply } from '@/entities/comment/api/useCommentReply';
import { Button, Textarea } from '@/shared/ui';
import type { Dispatch, SetStateAction } from 'react';
import { useForm, type RegisterOptions } from 'react-hook-form';

interface CommentReplyFormProps {
  suggestionId: string;
  commentId: string;
  formOptions?: RegisterOptions<IFormInput, 'text'> | undefined;
  replyCommentId: string | null;
  setReplyCommentId: Dispatch<SetStateAction<string | null>>;
}

interface IFormInput {
  text: string;
}

export function CommentReplyForm({
  suggestionId,
  commentId,
  formOptions,
  replyCommentId,
  setReplyCommentId,
}: CommentReplyFormProps) {
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<IFormInput>();

  const { mutate, isPending, isError } = useCommentReply(
    suggestionId,
    commentId
  );

  return (
    <form
      onSubmit={handleSubmit(data =>
        mutate(data, {
          onSuccess: () => setReplyCommentId(null),
        })
      )}
      className="flex w-full flex-col gap-3 mt-3"
    >
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

      <div className="flex justify-end gap-2">
        <Button
          type="button"
          variant="ghost"
          disabled={isPending}
          onClick={() => setReplyCommentId(null)}
        >
          Отмена
        </Button>

        <Button
          type="submit"
          disabled={isPending}
          className={isError ? 'border-red-500' : ''}
        >
          Ответить
        </Button>
      </div>
    </form>
  );
}
