import { Navigate, Outlet } from 'react-router-dom';

export const AuthRoute = () => {
    const token = localStorage.getItem('jwt');
    return token ? <Navigate to="/" replace /> : <Outlet />;
}