import React, { createContext, useContext, useState, useEffect } from 'react';
import { authService } from '../services/apiService';

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check if there's a user in sessionStorage (optional)
    const stored = sessionStorage.getItem('user');
    if (stored) {
      setUser(JSON.parse(stored));
    }
    setLoading(false);
  }, []);

  const login = async (email, password) => {
    const res = await authService.login(email, password);
    if (res.success) {
      setUser(res.user);
      sessionStorage.setItem('user', JSON.stringify(res.user));
    }
    return res;
  };

  const register = async (email, password, nombreCuenta) => {
    const res = await authService.register(email, password, nombreCuenta);
    if (res.success) {
      setUser(res.user);
      sessionStorage.setItem('user', JSON.stringify(res.user));
    }
    return res;
  };

  const logout = async () => {
    await authService.logout();
    setUser(null);
    sessionStorage.removeItem('user');
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
};