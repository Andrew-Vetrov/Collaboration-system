import { useQuery } from '@tanstack/react-query';
import { getProjectUsersQueryKey } from '../lib/getProjectUsersQueryKey';
import type { ProjectUserList } from '../model/types';
import { projectsApi } from '@/shared/api';

export function useProjectUsers(projectId: string) {
  return useQuery<ProjectUserList | undefined>({
    queryKey: getProjectUsersQueryKey(projectId),
    queryFn: async () => {
      const responce = await projectsApi.projectsProjectIdUsersGet(projectId);

      return responce.data.data;
    },
  });
}
