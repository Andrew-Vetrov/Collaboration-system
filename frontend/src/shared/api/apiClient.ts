import { Configuration, ProjectsApi, SuggestionsApi, CommentsApi} from './generated';

const config = new Configuration({
  basePath: import.meta.env.VITE_API_URL || 'http://localhost:8000',
  accessToken: localStorage.getItem('jwt') || undefined,
});

// export const projectsApi = new ProjectsApi(config);
// export const suggestionsApi = new SuggestionsApi(config);
// export const commentsApi = new CommentsApi(config);
export const getProjectsApit = () => new ProjectsApi(config);
export const getSuggectionApi = () => new SuggestionsApi(config);
export const GetCommentsApi = () => new CommentsApi(config);
// export type {ProjectBasic};