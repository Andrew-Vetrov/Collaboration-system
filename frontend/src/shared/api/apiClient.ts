import {
  Configuration,
  ProjectsApi,
  SuggestionsApi,
  CommentsApi,
  AuthorizeApi,
} from './generated';

const config = new Configuration({
  basePath: import.meta.env.VITE_API_URL || 'http://localhost:8080',
  accessToken: () => localStorage.getItem('jwt') ?? '',
});

export const projectsApi = new ProjectsApi(config);
export const suggestionsApi = new SuggestionsApi(config);
export const commentsApi = new CommentsApi(config);
export const authApi = new AuthorizeApi(config);
