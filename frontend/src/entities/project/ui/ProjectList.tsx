import { Link } from 'react-router-dom';
import { useProjects } from '../api/useProjects';

export const ProjectList = (): JSX.Element => {
  const { data: projects = [], isLoading, error } = useProjects();

  if (isLoading) return <p>Загрузка проектов...</p>;
  if (error) return <p>Ошибка загрузки проектов...</p>;

  return (
    <div>
      <h1>Мои проекты</h1>
      <ul>
        {projects.map(project => (
          <Link key={project.project_id} to={`/project/${project.project_id}`}>
            <h3>{project.name}</h3>
          </Link>
        ))}
      </ul>
    </div>
  );
};
