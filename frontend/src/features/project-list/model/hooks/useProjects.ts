import {useQuery} from '@tanstack/react-query';
import type { ProjectBasic} from  '@/entities/project';
import { getProjectsApit as getProjectsApi } from '@/shared/api';

export default function  useProjects() {
    return useQuery<ProjectBasic[]>({
        queryKey: ['projects'],
        queryFn: async() => {
            const api = getProjectsApi();
            const response = await api.projectsGet();
            return response.data.projects ?? [];
        },
    });
}