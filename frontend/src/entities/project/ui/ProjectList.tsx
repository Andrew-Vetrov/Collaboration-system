import { Link } from 'react-router-dom';
import { useProjects } from '../api/useProjects';
import { Card, CardDescription, CardTitle } from '@/shared/ui';
import type { FC } from 'react';
import { routes } from '@/shared/route';

export const ProjectList: FC = () => {
  const { data: projects = [], isLoading, error } = useProjects();

  if (isLoading) return <p>Загрузка проектов...</p>;
  if (error) return <p>Ошибка загрузки проектов...</p>;

  return (
    <div className="flex flex-col items-center w-screen">
      <h1 className="text-center text-3xl my-4">Мои проекты</h1>
      <div className="grid gap-4 w-[90%] sm:w-[70%] grid-cols-1">
        {projects.map(project => (
          <Link
            key={project.project_id}
            to={routes.projectRoute(project.project_id)}
            className="block"
          >
            <Card className="hover:shadow-md transition-shadow text-center w-full h-full">
              <CardTitle>
                <span className="font-semibold text-lg px-2 md:px-10 line-clamp-2">
                  {project.name}
                </span>
              </CardTitle>
              <CardDescription>
                <span className="font-semibold text-base px-2 md:px-10 line-clamp-2 whitespace-pre-line">
                  {project.description}
                </span>
              </CardDescription>
            </Card>
          </Link>
        ))}
      </div>
    </div>
  );
};
