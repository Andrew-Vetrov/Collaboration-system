import { useQuery } from '@tanstack/react-query';
import { getProjectRolesQueryKeys } from '../lib/getProjectRolesQueryKey';
import { rolesApi } from '@/shared/api';
import type { Role } from '../model/types';

export const useProjetctRoles = (projectId: string) => {
  return useQuery<Role[]>({
    queryKey: getProjectRolesQueryKeys(projectId),
    queryFn: async () => {
      const response = await rolesApi.projectsProjectIdRolesGet(projectId);
      return response.data.data ?? [];
    },
  });
};
