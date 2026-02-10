import { routes } from '@/shared/route';
import {
  MutationCache,
  QueryCache,
  QueryClient,
  QueryClientProvider,
} from '@tanstack/react-query';
import { AxiosError } from 'axios';

const handleUnauthorized = () => {
  localStorage.removeItem('jwt');
  queryClient.clear();
  window.location.href = routes.authRoute();
};

const queryClient = new QueryClient({
  queryCache: new QueryCache({
    onError: error => {
      if (error instanceof AxiosError && error.response?.status === 401) {
        handleUnauthorized();
      }
    },
  }),

  mutationCache: new MutationCache({
    onError: error => {
      if (error instanceof AxiosError && error.response?.status === 401) {
        handleUnauthorized();
      }
    },
  }),
});
interface QueryProviderProps {
  children: React.ReactNode;
}

export const QueryProvider = ({ children }: QueryProviderProps) => {
  return (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );
};
