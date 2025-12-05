import { Navigate, Outlet } from 'react-router-dom';

export const ProtectedRoute = () => {
  const token = localStorage.getItem('jwt');
  return token ? <Outlet /> : <Navigate to="/auth" replace />;
};
