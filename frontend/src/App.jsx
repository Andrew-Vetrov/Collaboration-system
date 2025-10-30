import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import { useEffect, useState } from 'react';
import { projectsApi } from './services/api';

function Home() {
  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8000/auth';
  };

  return (
    <div>
      <h1>Авторизация</h1>
      <button onClick={handleAuthClick}>Войти через Google</button>
    </div>
  );
}

function AuthSuccess() {
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

function Projects() {
  return (
    <div>
      <h1>Мои проекты</h1>
      <button>Создать проект</button>
    </div>
  );
}

export default function App() {
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
