import { useQuery } from '@tanstack/react-query';
import { getAuthMeQueryKey } from '../lib/getAuthMeQueryKey';
import { authApi } from '@/shared/api';
import type { CurrentUser } from '../model/types';

export function useAuthMe() {
  return useQuery<CurrentUser>({
    queryKey: getAuthMeQueryKey(),
    queryFn: async () => {
      const response = await authApi.authMeGet();
      return response.data;
    },
    retry: false,
  });
}
