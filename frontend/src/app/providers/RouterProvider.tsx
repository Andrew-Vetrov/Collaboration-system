import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { LoginPage } from '@/pages/login/LoginPage';
import { ProjectsPage } from '@/pages/projectsPage/ProjectsPage';
import { AuthSuccess } from '@/features/auth-by-google';
import { ProtectedRoute } from '@/shared/route/ProtectedRoute';
import { NotFoundPage } from '@/pages/not-found-page/NotFoundPage';
import { ProjectPage } from '@/pages/projectPage/ProjectPage';
import { AuthRoute } from '@/shared/route';

const router = createBrowserRouter([
  {
    element: <AuthRoute />,
    children: [
      { path: '/auth', element: <LoginPage /> },
      { path: '/auth/success', element: <AuthSuccess /> },
    ],
  },
  {
    element: <ProtectedRoute />,
    children: [
      { path: '/', element: <ProjectsPage /> },
      { path: '/project/:id', element: <ProjectPage /> },
    ],
  },
  {
    path: '*',
    element: <NotFoundPage />,
  },
]);

export const AppRouter = () => {
  return <RouterProvider router={router} />;
};
