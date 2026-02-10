import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { lazy } from 'react';
import { LoginPage } from '@/pages/login/';
import { AuthSuccess } from '@/features/auth-by-google';
import { ProtectedRoute } from '@/shared/route/ProtectedRoute';
import { NotFoundPage } from '@/pages/not-found-page/NotFoundPage';
import { AuthRoute } from '@/shared/route';
import { AppLayout } from '../layouts/app-layout/AppLayout';
import { ProjectLayout } from '../layouts/project-layout/ProjectLayout';

const ProjectsPage = lazy(() => import('@/pages/projects-page'));
const ProjectPage = lazy(() => import('@/pages/project-page'));
const SuggestionPage = lazy(() => import('@/pages/suggestion-page'));
const CreateSuggestionPage = lazy(
  () => import('@/pages/create-suggestion-page')
);

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
      {
        element: <AppLayout />,
        children: [
          { path: '/', element: <ProjectsPage /> },

          {
            path: '/projects/:projectId',
            element: <ProjectLayout />,
            children: [
              { index: true, element: <ProjectPage /> },

              {
                path: 'suggestions/:suggestionId',
                element: <SuggestionPage />,
              },

              {
                path: 'create-suggestion',
                element: <CreateSuggestionPage />,
              },
            ],
          },
        ],
      },
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
