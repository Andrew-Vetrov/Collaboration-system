import { RouterProvider, createBrowserRouter } from 'react-router-dom';
import { lazy } from 'react';
import { LoginPage } from '@/pages/login/';
import { AuthSuccess } from '@/features/auth-by-google';
import { ProtectedRoute } from '@/shared/route/ProtectedRoute';
import { NotFoundPage } from '@/pages/not-found-page/NotFoundPage';
import { AuthRoute, routesPaths } from '@/shared/route';
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
      { path: routesPaths.authPagePath, element: <LoginPage /> },
      { path: routesPaths.authSuccessPagePath, element: <AuthSuccess /> },
    ],
  },
  {
    element: <ProtectedRoute />,
    children: [
      {
        element: <AppLayout />,
        children: [
          { path: routesPaths.projectsPagePath, element: <ProjectsPage /> },

          {
            path: routesPaths.projectPagePath,
            element: <ProjectLayout />,
            children: [
              { index: true, element: <ProjectPage /> },

              {
                path: routesPaths.suggestionPagePath,
                element: <SuggestionPage />,
              },

              {
                path: routesPaths.createSuggestionPagePath,
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
