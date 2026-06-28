import React, { useState } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useToast } from '../contexts/ToastContext';
import { Link, useNavigate } from 'react-router-dom';
import './Login.css';
import FormModal from '../components/FormModal';

const LoginPage = () => {
  const { login } = useAuth();
  const { addToast } = useToast();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    try {
      const res = await login(email, password);
      if (!res.success) {
        setError(res.message || 'Credenciales inválidas');
        addToast(res.message || 'Error al iniciar sesión', 'error');
      } else {
        // Successful login, redirect based on role
        const role = res.user.idRol;
        if (role === 2) {
          navigate('/admin', { replace: true });
        } else {
          navigate('/user', { replace: true });
        }
      }
    } catch (err) {
      setError('Error de red');
      addToast('Error de red', 'error');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <h2>Iniciar Sesión</h2>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label>Email</label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              disabled={loading}
            />
          </div>
          <div className="form-group">
            <label>Contraseña</label>
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              disabled={loading}
            />
          </div>
          {error && <div className="error-message">{error}</div>}
          <button type="submit" disabled={loading} className="btn btn-primary w-full">
            {loading ? 'Ingresando...' : 'Iniciar Sesión'}
          </button>
        </form>
        <div className="auth-footer">
          <p>
            ¿No tienes cuenta? <Link to="/register">Crear una</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default LoginPage;