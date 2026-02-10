export const routes = {
  authRoute: () => '/auth',
  projectsRoute: () => '/',
  projectRoute: (projectId: string) => `/projects/${projectId}`,
  suggestionRoute: (projectId: string, suggestionId: string) =>
    `/projects/${projectId}/suggestions/${suggestionId}`,
  createSuggestionRoute: (projectId: string) =>
    `/projects/${projectId}/create-suggestion`,
};

export const routesPaths = {
  authPagePath: '/auth',
  authSuccessPagePath: '/auth/success',
  projectsPagePath: '/',
  projectPagePath: '/projects/:projectId',
  suggestionPagePath: 'suggestions/:suggestionId',
  createSuggestionPagePath: 'create-suggestion',
};
