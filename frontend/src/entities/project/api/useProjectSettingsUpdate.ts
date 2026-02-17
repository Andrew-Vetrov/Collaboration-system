import { useMutation, useQueryClient } from '@tanstack/react-query';
import type { ProjectSettings, ProjectsProjectIdSettingsPutRequest } from '../model/types';
import { projectsApi } from '@/shared/api';
import { getProjectSettingsQueryKey } from '../lib/getProjectSettingsQueryKey';
import { getProjectsQueryKey } from '../lib/getProjectsQueryKey';

export function useProjectSettingsUpdate(projectId: string) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (data: ProjectsProjectIdSettingsPutRequest) => {
      const response = await projectsApi.projectsProjectIdSettingsPut(
        projectId,
        data
      );
      return response;
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
