import { Navigate, Outlet } from 'react-router-dom';

export const ProtectedRoute = () => {
  const token = localStorage.getItem('jwt');
  console.log(token);
  return token ? <Outlet /> : <Navigate to="/auth" replace />;
};