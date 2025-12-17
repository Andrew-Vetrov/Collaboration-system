import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { lazy } from 'react';
import { LoginPage } from '@/pages/login/';
import { AuthSuccess } from '@/features/auth-by-google';
import { ProtectedRoute } from '@/shared/route/ProtectedRoute';
import { NotFoundPage } from '@/pages/not-found-page/NotFoundPage';
import { AuthRoute } from '@/shared/route';

const ProjectsPage = lazy(() => import('@/pages/projects-page'));
const ProjectPage = lazy(() => import('@/pages/project-page'));
const SuggestionPage = lazy(() => import('@/pages/suggestion-page'));

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
      { path: '/suggestion/:id', element: <SuggestionPage /> },
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
