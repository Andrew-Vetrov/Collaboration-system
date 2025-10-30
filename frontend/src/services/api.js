import { 
  ProjectsApi, 
  SuggestionsApi, 
  CommentsApi, 
  AuthorizeApi, 
  InvitesApi,
  ApiClient
} from '../api/src';

const apiClient = new ApiClient();
apiClient.basePath = 'http://localhost:8000';

delete apiClient.defaultHeaders['User-Agent'];

export const projectsApi = new ProjectsApi(apiClient);
export const suggestionsApi = new SuggestionsApi(apiClient);
export const commentsApi = new CommentsApi(apiClient);
export const authorizeApi = new AuthorizeApi(apiClient);
export const invitesApi = new InvitesApi(apiClient);