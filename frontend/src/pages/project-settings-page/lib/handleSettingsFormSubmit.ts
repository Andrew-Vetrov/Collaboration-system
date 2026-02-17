import type { ProjectsProjectIdSettingsPutRequest } from '@/entities/project/model/types';
import type { SettingsFormInput } from '../model/types';

export const handleSettingsFormSubmit = (
  formData: SettingsFormInput,
  mutateFunction: (data: ProjectsProjectIdSettingsPutRequest) => void
) => {
  const data = {
    name: formData.name,
    description: formData.description,
    vote_interval: `${formData.vote_interval.value} ${formData.vote_interval.unit}`,
    votes_for_interval: formData.votes_for_interval,
  };

  mutateFunction(data);
};
