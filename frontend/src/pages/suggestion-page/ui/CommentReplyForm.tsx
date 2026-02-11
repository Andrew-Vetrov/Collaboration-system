import { useCommentCreate } from '@/entities/comment/api/useCommentCreate';
import { useCommentReply } from '@/entities/comment/api/useCommentReply';
import type { ReplyState } from '@/entities/comment/model/types';
import { Button, Textarea } from '@/shared/ui';
import type { Dispatch, SetStateAction } from 'react';
import { useForm, type RegisterOptions } from 'react-hook-form';

interface CommentReplyFormProps {
  suggestionId: string;
  commentId: string;
  formOptions?: RegisterOptions<IFormInput, 'text'> | undefined;
  isReplyOpen: ReplyState;
  setReplyOpen: Dispatch<SetStateAction<ReplyState>>;
}

interface IFormInput {
  text: string;
}

export function CommentReplyForm({
  suggestionId,
  commentId,
  formOptions,
  isReplyOpen,
  setReplyOpen,
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
          onSuccess: () => setReplyOpen({ isOpen: false, commentId: null }),
        })
      )}
      className="flex w-full flex-col gap-3"
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
          onClick={() => setReplyOpen({ isOpen: false, commentId: null })}
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
