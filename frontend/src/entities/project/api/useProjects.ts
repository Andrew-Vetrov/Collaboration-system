import { useQuery } from '@tanstack/react-query';
import type { ProjectBasic } from '@/entities/project';
import { projectsApi } from '@/shared/api';
import { getProjectsQueryKey } from '../lib/getProjectsQueryKey';

export function useProjects() {
  return useQuery<ProjectBasic[]>({
    queryKey: getProjectsQueryKey(),
    queryFn: async () => {
      const response = await projectsApi.projectsGet();
      return response.data.projects ?? [];
    },
  });
}
