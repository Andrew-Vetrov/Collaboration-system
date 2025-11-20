import {useQuery} from '@tanstack/react-query';
import type { ProjectBasic} from  '@/entities/project';
import { projectsApi } from '@/shared/api';

export function  useProjects() {
    return useQuery<ProjectBasic[]>({
        queryKey: ['projects'],
        queryFn: async() => {
            const response = await projectsApi.projectsGet();
            return response.data.projects ?? [];
        },
    });
}