import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { HomePage } from '../../pages/home/HomePage';
import { ProjectsPage } from '../../pages/project/ProjectPage';
import { AuthSuccess } from '../../features/auth-by-google';
import { ProtectedRoute } from '../../shared/route/ProtectedRoute';
import { NotFoundPage } from '../../pages/not-found-page/NotFoundPage';

const router = createBrowserRouter([
  { path: '/', element: <HomePage /> },
  { path: '/auth/success', element: <AuthSuccess /> },
  {
    element: <ProtectedRoute />,
    children: [{ path: '/projects', element: <ProjectsPage /> }],
  },
  {
    path: '*', element: <NotFoundPage />
  },
]);

export const AppRouter = () => {
  return <RouterProvider router={router} />;
};