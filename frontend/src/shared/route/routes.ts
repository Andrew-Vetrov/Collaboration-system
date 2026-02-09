export const routes = {
  authRoute: () => '/auth',
  projectsRoute: () => '/',
  projectRoute: (projectId: string) => `/project/${projectId}`,
  suggestionRoute: (projectId: string, suggestionId: string) =>
    `/project/${projectId}/suggestions/${suggestionId}`,
  createSuggestionRoute: (projectId: string) =>
    `/project/${projectId}/create-suggestion`,
};
