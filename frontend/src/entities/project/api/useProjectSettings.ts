import { useQuery } from '@tanstack/react-query';
import { getProjectSettingsQueryKey } from '../lib/getProjectSettingsQueryKey';
import type { ProjectSettings } from '../model/types';
import { projectsApi } from '@/shared/api';

export function useProjectSettings(projectId: string) {
  return useQuery<ProjectSettings>({
    queryKey: getProjectSettingsQueryKey(projectId),
    queryFn: async () => {
      const response =
        await projectsApi.projectsProjectIdSettingsGet(projectId);

      const data = response.data.data;

      if (!data) {
        throw new Error('Настройки проекта не получены (data is undefined)');
      }

      return data;
    },
  });
}
