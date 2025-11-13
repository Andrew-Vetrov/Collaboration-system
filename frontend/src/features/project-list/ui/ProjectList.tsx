import useProjects from '../model/hooks/useProjects'

export const ProjectList = (): JSX.Element => {
    const {data: projects = [], isLoading, error} = useProjects()

    if (isLoading) return <p>Загрузка проектов...</p>;
    if (error) return <p>Ошибка загрузки проектов...</p>;

    return (
    <div>
        <h1>Мои проекты</h1>
        <ul>
        {projects.map((project) => (
            <li key={project.project_id}>
            <h3>{project.name}</h3>
            <p>{project.description}</p>
            </li>
        ))}
        </ul>
    </div>
    );
}
