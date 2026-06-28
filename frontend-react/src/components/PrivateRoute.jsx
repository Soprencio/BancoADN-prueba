import React from 'react';
import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../contexts/AuthContext';

const PrivateRoleRoute = ({ role, children }) => {
  const { user, loading } = useAuth();
  if (loading) return <div>Cargando...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (user.idRol !== role) {
    // Redirect to appropriate dashboard
    return user.idRol === 1 ? <Navigate to="/usuario" replace /> : <Navigate to="/admin" replace />;
  }
  return <Outlet />;
};

export default PrivateRoleRoute;