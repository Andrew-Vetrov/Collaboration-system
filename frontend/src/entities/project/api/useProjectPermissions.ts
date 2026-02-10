import { useQuery } from '@tanstack/react-query';
import { getProjectPermissionsQueryKey } from '../lib/getProjectPermissionsQueryKey';
import { projectsApi } from '@/shared/api';
import type { ProjectPermissions } from '@/shared/api/generated';

export function useProjectPermissions(project_id?: string) {
  return useQuery<ProjectPermissions | undefined>({
    queryKey: project_id ? getProjectPermissionsQueryKey(project_id) : [],
    queryFn: async () => {
      if (!project_id) throw new Error('project_id is required');
      const response =
        await projectsApi.projectsProjectIdPermissionsMeGet(project_id);
      return response.data;
    },
    enabled: !!project_id,
    staleTime: 60_000,
  });
}
