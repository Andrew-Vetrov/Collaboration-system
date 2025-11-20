import { QueryProvider } from './providers/QueryProvider';
import { AppRouter } from './providers/RouterProvider';

export const App = () => {
  return (
    <QueryProvider>
      <AppRouter />
    </QueryProvider>
  );
};
