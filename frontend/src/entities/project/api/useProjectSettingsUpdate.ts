import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { ProjectSettings } from '../model/types';
import { projectsApi } from '@/shared/api';
import { getProjectSettingsQueryKey } from '../lib/getProjectSettingsQueryKey';
import { getProjectsQueryKey } from '../lib/getProjectsQueryKey';

export function useProjectSettingsUpdate(projectId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: ProjectSettings) => {
      const responce = await projectsApi.projectsProjectIdSettingsPut(
        projectId,
        data
      );
      return responce;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: getProjectSettingsQueryKey(projectId),
      });
      queryClient.invalidateQueries({
        queryKey: getProjectsQueryKey(),
      });
    },
  });
}
