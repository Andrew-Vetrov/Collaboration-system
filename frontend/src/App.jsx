import { useEffect, useState } from 'react';
import { projectsApi, authorizeApi } from './services/api';

function App() {
  const [projects, setProjects] = useState([]);

  useEffect(() => {
    const fetchProjects = async () => {
      try {
        const response = await projectsApi.projectsGet();
        console.log('Projects:', response);
      } catch (error) {
        console.error('Failed to load projects:', error);
      }
    };

    fetchProjects();
  }, []);

  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8000/auth';
  };

  const handleAddProject = async () => {
    try {
      const newProject = {
        name: 'Новый проект',
        description: 'Описание нового проекта'
      };

      const response = await projectsApi.projectsPost({ body: newProject }); 

    } catch (error) {
      console.error('Failed to create project:', error);
    }
  };

  return (
    <div>
      <h1>Мои проекты</h1>
      <button onClick={handleAuthClick}>Войти через Google</button>
      <button onClick={handleAddProject}>Создать проект</button>

    </div>
  );
}

export default App;
