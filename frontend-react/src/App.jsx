import React from 'react';
import { BrowserRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import LoginPage from './pages/Login';
import RegisterPage from './pages/Register';
import UserDashboard from './pages/UserDashboard';
import AdminDashboard from './pages/AdminDashboard';
import { useAuth } from './contexts/AuthContext';
import { AuthProvider } from './contexts/AuthContext';
import { ToastProvider } from './contexts/ToastContext';
import './App.css';

// Helper route that requires authentication and optional role
const PrivateRoute = ({ children, role }) => {
  const { user, loading } = useAuth();
  const location = useLocation();

  if (loading) return <div>Cargando...</div>;

  if (!user) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (role && user.idRol !== role) {
    // Not authorized for this role
    return <Navigate to="/" replace />;
  }

  return children;
};

function App() {
  return (
    <AuthProvider>
      <ToastProvider>
        <BrowserRouter>
          <Routes>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/register" element={<RegisterPage />} />
            <Route
              path="/user"
              element={
                <PrivateRole role={1}>
                  <UserDashboard />
                </PrivateRole>
              }
            />
            <Route
              path="/admin"
              element={
                <PrivateRole role={2}>
                  <AdminDashboard />
                </PrivateRole>
              }
            />
            {/* Redirect root to login */}
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </BrowserRouter>
      </ToastProvider>
    </AuthProvider>
  );
}

// Wrapper for role-based protection
const PrivateRole = ({ role, children }) => {
  const { user, loading } = useAuth();
  if (loading) return <div>Cargando...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (user.idRol !== role) {
    // Optionally redirect to a not found page
    return <Navigate to="/" replace />;
  }
  return children;
};

export default App;