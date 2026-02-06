import { useForm, useWatch } from 'react-hook-form';
import { useEffect, useRef } from 'react';
import { useDebounce } from '@/shared/hooks/useDebounce';
import type { IFormInput, Suggestion } from '@/entities/suggestion/model/types';
import type { SuggestionsSuggestionIdPutRequest } from '@/shared/api/generated';
import type { UseMutateFunction } from '@tanstack/react-query';

interface EditSuggestionFeatureParams {
  suggestion: Suggestion | null;
  handleSuggestionUpdate: UseMutateFunction<
    Suggestion,
    unknown,
    SuggestionsSuggestionIdPutRequest,
    unknown
  >;
  handleSuggestionDelete: UseMutateFunction<void, unknown, void, unknown>;
  onClose: (s: Suggestion | null) => void;
}

export const useEditSuggestionFeature = ({
  suggestion,
  handleSuggestionUpdate,
  handleSuggestionDelete,
  onClose,
}: EditSuggestionFeatureParams) => {
  const { register, handleSubmit, reset, control, formState } =
    useForm<IFormInput>({
      mode: 'onChange',
      defaultValues: { name: '', description: '' },
    });

  const lastSavedRef = useRef<IFormInput>({
    name: suggestion?.name || '',
    description: suggestion?.description || '',
  });

  const isValidForAutosave = (data: Partial<IFormInput>) => {
    if (!data.name || data.name.trim().length === 0) return false;
    if (!data.description || data.description.trim().length === 0) return false;
    return true;
  };

  useEffect(() => {
    if (suggestion) {
      const initial: IFormInput = {
        name: suggestion.name,
        description: suggestion.description || '',
      };
      reset(initial, {
        keepDirty: false,
        keepTouched: false,
        keepIsValid: true,
      });
      lastSavedRef.current = initial;
    }
  }, [suggestion, reset]);

  const formValues = useWatch({ control });
  const debounceFormData = useDebounce(formValues, 10000);

  const saveIfChanged = (data: Partial<IFormInput>) => {
    if (!suggestion) {
      return;
    }

    if (!isValidForAutosave(data)) return;

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
    handleSuggestionUpdate({ ...next, status: suggestion.status });
  };

  useEffect(() => saveIfChanged(debounceFormData), [debounceFormData]);

  const handleBlur = () => saveIfChanged(formValues);
  const handlePublish = (data: IFormInput) => {
    if (!suggestion) return;
    handleSuggestionUpdate(
      { ...data, status: 'discussion' },
      { onSuccess: () => onClose(null) }
    );
  };
  const handleDelete = () => {
    if (!suggestion) return;
    handleSuggestionDelete(undefined, { onSuccess: () => onClose(null) });
  };

  return {
    register,
    handleSubmit,
    control,
    formState,
    handlePublish,
    handleDelete,
    handleBlur,
  };
};
