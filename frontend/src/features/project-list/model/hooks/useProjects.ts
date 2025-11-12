import {useQuery} from '@tanstack/react-query';
import { ProjectBasic} from  '../../../../entities/project';
import { projectsApi } from '../../../../shared/api';

export default function  useProjects() {
    return useQuery<ProjectBasic[]>({
        queryKey: ['projects'],
        queryFn: async() => {
            const response = await projectsApi.projectsGet();
            return response.data.projects ?? [];
        },
    });
}