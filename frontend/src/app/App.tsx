import { QueryProvider } from './providers/QueryProvider';
import { AppRouter } from './providers/RouterProvider';
import './styles/index.css';

export const App = () => {
  return (
    <QueryProvider>
      <AppRouter />
    </QueryProvider>
  );
};
