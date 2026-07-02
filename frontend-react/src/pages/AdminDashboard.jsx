import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { solicitudesService, perfilesService, logsService } from '../services/apiService';
import { useToast } from '../contexts/ToastContext';
import ConfirmationModal from '../components/ConfirmationModal';
import FormModal from '../components/FormModal';
import './AdminDashboard.css';

function AdminDashboard() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { addToast } = useToast();

  const [vistaActiva, setVistaActiva] = useState('pendientes');
  const [solicitudesPendientes, setSolicitudesPendientes] = useState([]);
  const [perfilesList, setPerfilesList] = useState([]);
  const [logsList, setLogsList] = useState([]);
  const [ultimasList, setUltimasList] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [searchType, setSearchType] = useState('Todos');
  const [confirmModal, setConfirmModal] = useState({ open: false, mensaje: '', onConfirm: null });
  const [editModal, setEditModal] = useState({ open: false, perfil: null });
  const [loading, setLoading] = useState(false);

  const cargarPendientes = async () => {
    if (!user) return;
    setLoading(true);
    try {
      const pendientes = await solicitudesService.getPending(user.email);
      setSolicitudesPendientes(pendientes);
    } catch (error) {
      addToast('Error al cargar solicitudes pendientes', 'error');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const cargarPerfiles = async (term, type) => {
    if (!user) return;
    setLoading(true);
    try {
      const perfiles = await perfilesService.buscarPerfiles(term || '', type || 'Todos', user.idRol, user.email);
      setPerfilesList(perfiles);
    } catch (error) {
      addToast('Error al cargar perfiles', 'error');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const cargarLogs = async () => {
    if (!user) return;
    setLoading(true);
    try {
      const logs = await logsService.getLogs();
      setLogsList(logs);
    } catch (error) {
      addToast('Error al cargar logs', 'error');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const cargarUltimas = async () => {
    if (!user) return;
    setLoading(true);
    try {
      const ultimas = await solicitudesService.getRecent(user.email);
      setUltimasList(ultimas);
    } catch (error) {
      addToast('Error al cargar últimas solicitudes', 'error');
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarPendientes();
  }, [user]);

  useEffect(() => {
    if (vistaActiva === 'pendientes') {
      cargarPendientes();
    } else if (vistaActiva === 'perfiles') {
      cargarPerfiles('', 'Todos');
    } else if (vistaActiva === 'logs') {
      cargarLogs();
    } else if (vistaActiva === 'ultimas') {
      cargarUltimas();
    }
  }, [vistaActiva, user]);

  const handleAprobar = async (id) => {
    if (!user) return;
    setConfirmModal({
      open: true,
      mensaje: `¿Aprobás la solicitud #${id}?`,
      onConfirm: async () => {
        try {
          await solicitudesService.approveRequest(id, user.email);
          addToast('Solicitud aprobada', 'success');
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
          await cargarPendientes();
        } catch (error) {
          addToast('Error al aprobar la solicitud', 'error');
          console.error(error);
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
        }
      },
    });
  };

  const handleRechazar = async (id) => {
    if (!user) return;
    setConfirmModal({
      open: true,
      mensaje: `¿Rechazás la solicitud #${id}?`,
      onConfirm: async () => {
        try {
          await solicitudesService.rejectRequest(id, user.email);
          addToast('Solicitud rechazada', 'success');
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
          await cargarPendientes();
        } catch (error) {
          addToast('Error al rechazar la solicitud', 'error');
          console.error(error);
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
        }
      },
    });
  };

  const handleDarDeBaja = async (perfil) => {
    if (!user) return;
    setConfirmModal({
      open: true,
      mensaje: `¿Dás de baja el perfil de ${perfil.nombreCompleto}?`,
      onConfirm: async () => {
        try {
          await perfilesService.deactivateProfile(perfil.idPerfil, user.email);
          addToast('Perfil dado de baja', 'success');
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
          await cargarPerfiles(searchTerm, searchType);
        } catch (error) {
          addToast('Error al dar de baja el perfil', 'error');
          console.error(error);
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
        }
      },
    });
  };

  const handleRestaurar = async (perfil) => {
    if (!user) return;
    setConfirmModal({
      open: true,
      mensaje: `¿Restaurás el perfil de ${perfil.nombreCompleto}?`,
      onConfirm: async () => {
        try {
          await perfilesService.reactivateProfile(perfil.idPerfil, user.email);
          addToast('Perfil restaurado', 'success');
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
          await cargarPerfiles(searchTerm, searchType);
        } catch (error) {
          addToast('Error al restaurar el perfil', 'error');
          console.error(error);
          setConfirmModal({ open: false, mensaje: '', onConfirm: null });
        }
      },
    });
  };

  const handleEditarSubmit = async (values) => {
    if (!user || !editModal.perfil) return;
    try {
      await perfilesService.updateProfile(editModal.perfil.idPerfil, values, user.email);
      addToast('Perfil modificado', 'success');
      setEditModal({ open: false, perfil: null });
      await cargarPerfiles(searchTerm, searchType);
    } catch (error) {
      addToast('Error al modificar el perfil', 'error');
      console.error(error);
      setEditModal({ open: false, perfil: null });
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="admin-dashboard">
      <header className="topbar">
        <div className="topbar-left">
          {user?.email}
        </div>
        <div className="topbar-center">
          <h1>
            <span style={{ color: '#e25c6b' }}>Simple</span>
            <span style={{ color: '#2b94c8' }}>ADN</span>
          </h1>
        </div>
        <div className="topbar-right">
          <button
            className={vistaActiva === 'pendientes' ? 'active' : ''}
            onClick={() => setVistaActiva('pendientes')}
          >
            Solicitudes Pendientes
          </button>
          <button
            className={vistaActiva === 'perfiles' ? 'active' : ''}
            onClick={() => setVistaActiva('perfiles')}
          >
            Administrar Perfiles
          </button>
          <button
            className={vistaActiva === 'logs' ? 'active' : ''}
            onClick={() => setVistaActiva('logs')}
          >
            Logs
          </button>
          <button
            className={vistaActiva === 'ultimas' ? 'active' : ''}
            onClick={() => setVistaActiva('ultimas')}
          >
            Últimas Solicitudes
          </button>
          <button onClick={handleLogout}>Cerrar Sesión</button>
        </div>
      </header>

      <main className="main-content">
        {vistaActiva === 'pendientes' && (
          <div className="vista-pendientes">
            <h2>Solicitudes Pendientes</h2>
            {solicitudesPendientes.length === 0 ? (
              <p className="empty-state">No hay solicitudes pendientes.</p>
            ) : (
              solicitudesPendientes.map(sol => (
                <div key={sol.idSolicitud} className="solicitud-card">
                  <div className="card-info">
                    <strong>{sol.email}</strong>
                    <span className="chip tipo">{sol.tipo}</span>
                    <span>{sol.fechaSolicitud}</span>
                    {(sol.tipo === 'REGISTRAR' || sol.tipo === 'MODIFICAR') && (
                      <>
                        <p>Nombre: {sol.nombreCompleto}</p>
                        <p>Código: {sol.codigoSecuencia}</p>
                        <p>Descripción: {sol.descripcion}</p>
                      </>
                    )}
                  </div>
                  <div className="card-actions">
                    <button className="btn btn-success" onClick={() => handleAprobar(sol.idSolicitud)}>
                      Aprobar
                    </button>
                    <button className="btn btn-danger" onClick={() => handleRechazar(sol.idSolicitud)}>
                      Rechazar
                    </button>
                  </div>
                </div>
              ))
            )}
          </div>
        )}

        {vistaActiva === 'perfiles' && (
          <div className="vista-perfiles">
            <h2>Administrar Perfiles</h2>
            <div className="search-bar">
              <select value={searchType} onChange={e => setSearchType(e.target.value)}>
                <option value="Todos">Todos</option>
                <option value="nombre">Por Nombre</option>
                <option value="codigo">Por Código</option>
              </select>
              <input
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                placeholder="Buscar..."
              />
              <button onClick={() => cargarPerfiles(searchTerm, searchType)}>
                Buscar
              </button>
            </div>
            {perfilesList.map(p => (
              <div key={p.idPerfil} className="perfil-card">
                <div className="card-info">
                  <strong>{p.nombreCompleto}</strong>
                  <span className={`status-chip ${p.estado === 1 ? 'active' : 'inactive'}`}>
                    {p.estado === 1 ? 'Activo' : 'Inactivo'}
                  </span>
                  <p>Código: {p.codigoSecuencia}</p>
                  <p>Fecha: {p.fechaMuestra}</p>
                  <p>{p.descripcion}</p>
                </div>
                <div className="card-actions">
                  <button onClick={() => setEditModal({ open: true, perfil: p })}>
                    Modificar
                  </button>
                  {p.estado === 1 ? (
                    <button className="btn btn-danger" onClick={() => handleDarDeBaja(p)}>
                      Dar de baja
                    </button>
                  ) : (
                    <button className="btn btn-success" onClick={() => handleRestaurar(p)}>
                      Restaurar
                    </button>
                  )}
                </div>
              </div>
            ))}
          </div>
        )}

        {vistaActiva === 'logs' && (
          <div className="vista-logs">
            <div className="vista-header">
              <h2>Logs del Sistema</h2>
              <button onClick={cargarLogs}>Actualizar</button>
            </div>
            {logsList.map(log => (
              <div key={log.idRegistro} className="log-entry">
                <span className="log-id">#{log.idRegistro}</span>
                <span className="log-responsable">
                  {log.nombreCuenta}
                  {log.esAdmin && <span className="chip admin">Admin</span>}
                </span>
                <span className="log-email">{log.email}</span>
                <span className="log-accion">{log.acciones}</span>
                {log.descripcion && <span className="log-desc">{log.descripcion}</span>}
                <span className="log-fecha">{log.fecha}</span>
              </div>
            ))}
          </div>
        )}

        {vistaActiva === 'ultimas' && (
          <div className="vista-ultimas">
            <h2>Últimas Solicitudes Resueltas</h2>
            {ultimasList.map(sol => (
              <div key={sol.idSolicitud} className="solicitud-card">
                <span className="chip tipo">{sol.tipo}</span>
                <span>{sol.email}</span>
                <span className={`status-chip ${sol.estado === 1 ? 'active' : 'inactive'}`}>
                  {sol.estado === 1 ? 'Aprobada' : 'Rechazada'}
                </span>
                <span>{sol.fechaSolicitud}</span>
              </div>
            ))}
          </div>
        )}
      </main>

      <ConfirmationModal
        isOpen={confirmModal.open}
        onClose={() => setConfirmModal({ open: false, mensaje: '', onConfirm: null })}
        title="Confirmar acción"
        message={confirmModal.mensaje}
        confirmLabel="Confirmar"
        onConfirm={() => { confirmModal.onConfirm?.(); }}
      />

      <FormModal
        isOpen={editModal.open}
        onClose={() => setEditModal({ open: false, perfil: null })}
        title="Modificar Perfil"
        fields={[
          { name: 'nombreCompleto', label: 'Nombre Completo', required: true },
          { name: 'codigoSecuencia', label: 'Código de Secuencia', required: true },
          { name: 'descripcion', label: 'Descripción', required: true },
        ]}
        initialValues={editModal.perfil ? {
          nombreCompleto: editModal.perfil.nombreCompleto,
          codigoSecuencia: editModal.perfil.codigoSecuencia,
          descripcion: editModal.perfil.descripcion,
        } : {}}
        submitLabel="Guardar Cambios"
        onSubmit={handleEditarSubmit}
      />
    </div>
  );
}

export default AdminDashboard;