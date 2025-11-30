import { Link } from 'react-router-dom';
import { useProjects } from '../api/useProjects';
import { Card, CardDescription, CardTitle } from '@/shared/ui';

export const ProjectList = (): JSX.Element => {
  const { data: projects = [], isLoading, error } = useProjects();

  if (isLoading) return <p>Загрузка проектов...</p>;
  if (error) return <p>Ошибка загрузки проектов...</p>;

  return (
    <div className="flex flex-col items-center w-screen">
      <h1 className="text-center text-3xl mb-4">Мои проекты</h1>
      <div className="grid gap-4 w-[70%]">
        {projects.map(project => (
          <Link
            key={project.project_id}
            to={`/project/${project.project_id}`}
            className="block"
          >
            <Card className="hover:shadow-md transition-shadow text-center w-full h-full">
              <CardTitle>
                <span className="font-semibold text-lg line-clamp-2 ml-20 mr-20">
                  {project.name}
                </span>
              </CardTitle>
              <CardDescription>
                <span className="font-semibold text-lg text-clamp-2 wrap-break-word ml-20 mr-20 line-clamp-2 whitespace-pre-line">
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
