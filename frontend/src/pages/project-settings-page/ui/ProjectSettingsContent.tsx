import { useProjectSettings } from '@/entities/project/api/useProjectSettings';
import { PROJECT_INTERVAL_UNITS } from '@/entities/project/model/types';
import {
  Button,
  Input,
  Label,
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
  Textarea,
} from '@/shared/ui';
import { Loader2 } from 'lucide-react';
import { useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import type { SettingsFormInput } from '../model/types';
import { useProjectSettingsUpdate } from '@/entities/project/api/useProjectSettingsUpdate';

interface ProjectSettingsContentProps {
  projectId: string;
}

export function ProjectSettingsContent({
  projectId,
}: ProjectSettingsContentProps) {
  const {
    data: settings,
    isLoading,
    isError,
    isSuccess,
  } = useProjectSettings(projectId);
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
    control,
  } = useForm<SettingsFormInput>({
    defaultValues: {
      name: '',
      description: '',
      vote_interval: { value: 1, unit: 'weeks' },
      votes_for_interval: 10,
    },
  });

  const { mutate: updateSettings, isPending: isUpdatingSettings } =
    useProjectSettingsUpdate(projectId);

  useEffect(() => {
    if (isSuccess && settings) {
      reset({
        name: settings.name,
        description: settings.description,
        votes_for_interval: settings.votes_for_interval,
        vote_interval: {
          value: Number(settings.vote_interval.split(' ')[0]),
          unit: (settings.vote_interval.split(' ')[1] || 'hours') as
            | 'minutes'
            | 'hours'
            | 'weeks'
            | 'months',
        },
      });
    }
  }, [isSuccess, settings, reset]);

  const onSubmit = handleSubmit(formData => {
    const transformedData = {
      ...formData,
      vote_interval: `${formData.vote_interval.value} ${formData.vote_interval.unit}`,
    };

    return updateSettings(transformedData);
  });

  if (isLoading) {
    return (
      <div className="flex items-center justify-center py-12">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (isError || !settings) {
    return (
      <div className="text-destructive">Ошибка загрузки настроек проекта</div>
    );
  }

  return (
    <form onSubmit={onSubmit} className="flex flex-col gap-4">
      <div className="flex flex-col gap-2">
        <Label htmlFor="name-id">Имя проекта</Label>
        <Input
          {...register('name', {
            maxLength: { message: 'Максимум 100 символов', value: 100 },
            required: 'Необходимо написать имя проекта',
          })}
          id="name-id"
        />
        {errors.name && (
          <p className="text-red-500 text-sm mt-1">{errors.name.message}</p>
        )}
      </div>
      <div className="flex flex-col gap-2">
        <Label htmlFor="description-1">Описание проекта</Label>
        <Textarea
          {...register('description', {
            required: 'Необходимо написать описание для проекта',
            maxLength: { message: 'Максимум 2000 символов', value: 2000 },
          })}
          id="description-1"
          name="description"
          className="max-h-60 overflow-auto"
        />
        {errors.description && (
          <p className="text-red-500 text-sm mt-1">
            {errors.description.message}
          </p>
        )}
      </div>
      <div className="flex sm:flex-row flex-col gap-2">
        <Label htmlFor="vote_interval-id">Период голосования</Label>
        <div id="vote_interval-id" className="flex gap-2">
          <Input
            {...register('vote_interval.value', {
              required: 'Необходимо задать интервал',
              min: { value: 1, message: 'Минимум 1' },
              max: { value: 100, message: 'Максимум 100' },
            })}
            type="number"
            className="max-w-20"
          />
          <Controller
            name="vote_interval.unit"
            control={control}
            render={({ field }) => (
              <Select onValueChange={field.onChange} defaultValue={field.value}>
                <SelectTrigger>
                  <SelectValue />
                </SelectTrigger>
                <SelectContent>
                  {Object.entries(PROJECT_INTERVAL_UNITS).map(
                    ([key, value]) => (
                      <SelectItem key={key} value={key}>
                        {value}
                      </SelectItem>
                    )
                  )}
                </SelectContent>
              </Select>
            )}
          />
        </div>
        {errors.vote_interval && (
          <p className="text-red-500 text-sm mt-1">
            {errors.vote_interval.value?.message}
          </p>
        )}
      </div>
      <div className="flex sm:flex-row flex-col gap-2">
        <Label htmlFor="votes_for_interval-id">
          Количество реакций за интервал
        </Label>
        <Input
          type="number"
          {...register('votes_for_interval', {
            required: 'Необходимо задать количество реакций',
            min: { value: 1, message: 'Минимум 1' },
            max: { value: 100, message: 'Максимум 100' },
            valueAsNumber: true,
          })}
          id="votes_for_interval-id"
          className="max-w-20"
        />
        {errors.votes_for_interval && (
          <p className="text-red-500 text-sm mt-1">
            {errors.votes_for_interval.message}
          </p>
        )}
      </div>
      <Button type="submit" className="self-end" disabled={isUpdatingSettings}>
        {isUpdatingSettings ? 'Сохранение...' : 'Сохранить'}
      </Button>
    </form>
  );
}
