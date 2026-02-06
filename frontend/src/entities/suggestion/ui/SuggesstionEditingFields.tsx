import { Button, Input, Textarea } from '@/shared/ui';
import type { Suggestion } from '../model/types';
import { Label } from '@radix-ui/react-label';
import { useForm, useWatch } from 'react-hook-form';
import { useEffect, useRef, type Dispatch, type SetStateAction } from 'react';
import { useDebounce } from '@/shared/hooks/useDebounce';
import type { SuggestionsSuggestionIdPutRequest } from '@/shared/api/generated';
import type { UseMutateFunction } from '@tanstack/react-query';

interface IFormInput {
  name: string;
  description: string;
}

interface SuggestionEditingFieldsProps {
  suggestion: Suggestion;
  handleSuggestionUpdate: UseMutateFunction<
    Suggestion,
    unknown,
    SuggestionsSuggestionIdPutRequest,
    unknown
  >;
  handleSuggestionDelete: UseMutateFunction<void, unknown, void, unknown>;
  onClose: Dispatch<SetStateAction<Suggestion | null>>;
}

export function SuggestionEditingFields(props: SuggestionEditingFieldsProps) {
  const {
    register,
    handleSubmit,
    reset,
    control,
    formState: { errors, isValid },
  } = useForm<IFormInput>({
    mode: 'onChange',
    defaultValues: {
      name: '',
      description: '',
    },
  });

  const lastSavedRef = useRef<IFormInput | null>(null);
  useEffect(() => {
    const initial: IFormInput = {
      name: props.suggestion.name,
      description: props.suggestion.description || '',
    };

    reset(initial, {
      keepDirty: false,
      keepTouched: false,
      keepIsValid: true,
    });

    lastSavedRef.current = initial;
  }, [props.suggestion, reset]);

  const formValues = useWatch({
    control,
  });
  const debounceFormData = useDebounce(formValues, 5000);

  const saveIfChanged = (data: Partial<IFormInput>) => {
    if (!isValid) {
      return;
    }
    const next: IFormInput = {
      name: data.name ?? '',
      description: data.description ?? '',
    };

    const last = lastSavedRef.current;
    if (
      last &&
      last.name === next.name &&
      last.description === next.description
    ) {
      return;
    }

    lastSavedRef.current = next;
    props.handleSuggestionUpdate({ ...next, status: props.suggestion.status });
  };

  useEffect(() => {
    saveIfChanged(debounceFormData);
  }, [debounceFormData]);

  const handleBlur = () => {
    saveIfChanged(formValues);
  };

  const handlePublish = (data: IFormInput) => {
    if (!isValid) {
      return;
    }
    props.handleSuggestionUpdate(
      {
        name: data.name,
        description: data.description,
        status: 'discussion',
      },
      {
        onSuccess: () => {
          props.onClose(null);
        },
      }
    );
  };

  return (
    <form onSubmit={handleSubmit(handlePublish)}>
      <Label htmlFor="name-id">Имя предложения</Label>
      <Input
        {...register('name', {
          required: 'Необходимо задать имя предложению',
          minLength: {
            value: 1,
            message: 'Имя не может быть пустым',
          },
          maxLength: {
            value: 30,
            message: 'Максимум 30 символов',
          },
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
          onBlur: handleBlur,
          minLength: {
            value: 1,
            message: 'Описание не может быть пустым',
          },
        })}
        id="description-id"
      ></Textarea>
      {errors.description && (
        <p className="text-red-500 text-sm mt-1">
          {errors.description.message}
        </p>
      )}

      <Button type="submit">Опубликовать</Button>
      <Button
        type="reset"
        onClick={() =>
          props.handleSuggestionDelete(undefined, {
            onSuccess: () => props.onClose(null),
          })
        }
      >
        Удалить черновик
      </Button>
    </form>
  );
}
