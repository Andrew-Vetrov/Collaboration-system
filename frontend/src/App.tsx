import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { projectsApi } from './apiClient';
import type { ProjectBasic } from './apiClient';


function Home(): JSX.Element {
  const handleAuthClick = (): void => {
    window.location.href = 'http://localhost:8000/auth';
  };

  return (
    <div>
      <h1>Авторизация</h1>
      <button onClick={handleAuthClick}>Войти через Google</button>
    </div>
  );
}

function AuthSuccess(): JSX.Element {
  const navigate = useNavigate();

  useEffect(() => {
    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');

    if (token) {
      localStorage.setItem('jwt', token);
      navigate('/projects');
    } else {
      navigate('/');
    }
  }, [navigate]);

  return <h2>Авторизация успешна! Перенаправляем...</h2>;
}

function Projects(): JSX.Element {

  const [projects, setProjects] = useState<ProjectBasic[]>([]);

  const handleLoadProjects = async ():Promise<void> => {
    try {
      const response = await projectsApi.projectsGet();
      console.log(response.data.projects);
      setProjects(response.data.projects ?? []);
      // const projects: ProjectBasic[] = response.data.projects;

    } catch (error) {
      console.log(error);
    }
  } 

  return (
    <div>
      <h1>Мои проекты</h1>
      <button onClick={handleLoadProjects}>Обновить список</button>
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

export default function App(): JSX.Element {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/auth/success" element={<AuthSuccess />} />
        <Route path="/projects" element={<Projects />} />
      </Routes>
    </Router>
  );
}
