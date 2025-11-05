import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { projectsApi } from './apiClient';
import type { ProjectBasic } from './apiClient';
import { QueryClient, QueryClientProvider, useQuery } from '@tanstack/react-query';



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
    const token = localStorage.getItem('jwt');
    if (!token) {
      const params = new URLSearchParams(window.location.search);
      const tokenFromUrl = params.get('token');

      if (tokenFromUrl) {
        localStorage.setItem('jwt', tokenFromUrl);
        navigate('/projects', { replace: true });
      } else {
        navigate('/', { replace: true });
      }
    } else {
      navigate('/projects', { replace: true });
    }
  }, [navigate]);


  return <h2>Авторизация успешна! Перенаправляем...</h2>;
}

function useProjects() {
  return useQuery<ProjectBasic[]>({
    queryKey: ['projects'],
    queryFn: async () => {
      const response = await projectsApi.projectsGet();
      return response.data.projects ?? [];
    },
  });
}

export function Projects(): JSX.Element {
  const { data: projects = [], isLoading, error } = useProjects();

  if (isLoading) return <p>Загрузка проектов...</p>;
  if (error) return <p>Ошибка загрузки проектов</p>;

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


export default function App(): JSX.Element {
  const queryClient = new QueryClient();

  return (
    <QueryClientProvider client={queryClient}>
      <Router>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/auth/success" element={<AuthSuccess />} />
          <Route path="/projects" element={<Projects />} />
        </Routes>
      </Router>
    </QueryClientProvider>
  );
}
