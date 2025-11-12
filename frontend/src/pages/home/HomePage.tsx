import { useEffect } from "react";
import {useNavigate} from 'react-router-dom'

export const HomePage = () => {
  const navigate = useNavigate();
  useEffect(() => {
    const token = localStorage.getItem("jwt");
    if(token) {
      navigate("/projects", {replace: true})
    }
  },[navigate])

  const handleAuthClick = () => {
    window.location.href = 'http://localhost:8000/auth';
  };

  return (
    <div>
      <h1>Авторизация</h1>
      <button onClick={handleAuthClick}>Войти через Google</button>
    </div>
  );
};