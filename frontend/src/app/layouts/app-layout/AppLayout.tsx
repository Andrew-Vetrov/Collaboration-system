import { Outlet } from 'react-router-dom';
import { Header } from '@/widgets/header';

export const AppLayout = () => {
  return (
    <>
      <Header />
      <Outlet />
    </>
  );
};
