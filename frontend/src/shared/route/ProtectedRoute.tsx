import { Header } from '@/widgets/header';
import { Navigate, Outlet } from 'react-router-dom';

export const ProtectedRoute = () => {
  const token = localStorage.getItem('jwt');
  if (!token) {
    return <Navigate to="/auth" replace />;
  }

  return (
    <>
      <Header />
      <Outlet />
    </>
  );
};
