import React, { useState, useEffect, useRef } from 'react';
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
  const [searchError, setSearchError] = useState('');
  const [confirmModal, setConfirmModal] = useState({ open: false, mensaje: '', onConfirm: null });
  const [editModal, setEditModal] = useState({ open: false, perfil: null });
  const [loading, setLoading] = useState(false);
  // Track last search type+term so we can fix incorrect LadoServer log descriptions
  // (LadoServer BuscarPerfiles reassigns the id param in the Todos/Nombre loops,
  //  causing "busco perfiles por id" to be logged instead of the correct message)
  const lastSearchRef = useRef({ type: 'Todos', term: '' });

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
    if (type === 'codigo' && term && term.trim() && isNaN(Number(term.trim()))) {
      setSearchError('El código debe ser un valor numérico');
      return;
    }
    setSearchError('');
    setLoading(true);
    try {
      lastSearchRef.current = { type: type || 'Todos', term: term || '' };
      const perfiles = await perfilesService.buscarPerfiles(term || '', type || 'Todos', user.email, true);
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
      const logs = await logsService.getLogs(user.email);
      // Fix LadoServer log bug: BuscarPerfiles reassigns the id param at
      // lines 373/383, causing "busco perfiles por id" for Name/Todos searches.
      // The most recent BuscarPerfiles log corresponds to the last search
      // performed in this session — correct its message if needed.
      const last = lastSearchRef.current;
      if (last.type === 'Nombre' || last.type === 'nombre' || last.type === 'Todos') {
        const targetIdx = logs.findIndex(l =>
          l.acciones === 'BuscarPerfiles' &&
          /^.+? busco perfiles por id con el siguiente: \d+$/.test(l.descripcion || '')
        );
        if (targetIdx !== -1) {
          const fixed = { ...logs[targetIdx] };
          const name = fixed.descripcion.match(/^(.+?) busco/)?.[1] || '';
          if (last.type === 'Nombre' || last.type === 'nombre') {
            fixed.descripcion = name + ' busco perfiles por nombre con el siguiente: ' + last.term;
          } else {
            fixed.descripcion = name + ' busco todos los perfiles';
          }
          logs[targetIdx] = fixed;
        }
      }
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
    if (!user) return;
    if (vistaActiva === 'pendientes') {
      cargarPendientes();
    } else if (vistaActiva === 'perfiles') {
      cargarPerfiles('', 'Todos');
    } else if (vistaActiva === 'logs') {
      cargarLogs();
    } else if (vistaActiva === 'ultimas') {
      cargarUltimas();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [vistaActiva, user]);

  useEffect(() => {
    if (!searchError) return;
    const t = setTimeout(() => setSearchError(''), 3000);
    return () => clearTimeout(t);
  }, [searchError]);

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
          <span className="material-symbols-outlined topbar-icon">account_circle</span>
          <span className="topbar-admin-email">{user?.email}</span>
        </div>
        <div className="topbar-center">
          <h1>
            <span className="brand-coral">Simple</span>
            <span className="brand-blue">ADN</span>
          </h1>
        </div>
        <nav className="topbar-right">
          <button
            className={vistaActiva === 'pendientes' ? 'nav-link nav-active' : 'nav-link'}
            onClick={() => setVistaActiva('pendientes')}
          >
            Solicitudes
          </button>
          <button
            className={vistaActiva === 'perfiles' ? 'nav-link nav-active' : 'nav-link'}
            onClick={() => setVistaActiva('perfiles')}
          >
            Perfiles
          </button>
          <button
            className={vistaActiva === 'logs' ? 'nav-link nav-active' : 'nav-link'}
            onClick={() => setVistaActiva('logs')}
          >
            Logs
          </button>
          <button
            className={vistaActiva === 'ultimas' ? 'nav-link nav-active' : 'nav-link'}
            onClick={() => setVistaActiva('ultimas')}
          >
            Últimas
          </button>
          <button className="btn-icon btn-logout" onClick={handleLogout} title="Cerrar Sesión">
            <span className="material-symbols-outlined">logout</span>
          </button>
        </nav>
      </header>

      <main className="main-content">
        {vistaActiva === 'pendientes' && (
          <div className="vista-pendientes">
            <div className="page-header">
              <div>
                <h2>Solicitudes Pendientes</h2>
                <p className="page-subtitle">Revisa y gestiona las solicitudes de alta y baja de perfiles genéticos.</p>
              </div>
            </div>
            {solicitudesPendientes.length === 0 ? (
              <div className="empty-state-card">
                <span className="material-symbols-outlined empty-icon">inbox</span>
                <p>No hay más solicitudes pendientes.</p>
              </div>
            ) : (
              <div className="solicitudes-grid">
                {solicitudesPendientes.map(sol => {
                  const tipoLower = sol.tipo?.toLowerCase() || 'registrar';
                  const chipIcons = { registrar: 'add_circle', modificar: 'edit', baja: 'remove_circle', restaurar: 'settings_backup_restore' };
                  const chipIcon = chipIcons[tipoLower] || 'circle';
                  return (
                    <div key={sol.idSolicitud} className="solicitud-card">
                      <div className="card-header">
                        <div>
                          <h3 className="card-title">{sol.email}</h3>
                          {sol.codigoSecuencia && <p className="card-ref">ID Ref: {sol.codigoSecuencia}</p>}
                        </div>
                        <span className={`chip-tipo chip-${tipoLower}`}>
                          <span className="material-symbols-outlined chip-icon">{chipIcon}</span>
                          {sol.tipo}
                        </span>
                      </div>
                      <p className="card-desc">
                        {sol.descripcion || (tipoLower === 'baja' ? 'Solicitud de baja de perfil genético.' : tipoLower === 'restaurar' ? 'Solicitud de restauración de perfil genético.' : 'Solicitud de registro de perfil genético.')}
                      </p>
                      <div className="card-footer">
                        <button className="btn-accept" onClick={() => handleAprobar(sol.idSolicitud)}>
                          <span className="material-symbols-outlined">check</span>
                          Aceptar
                        </button>
                        <button className="btn-reject" onClick={() => handleRechazar(sol.idSolicitud)}>
                          <span className="material-symbols-outlined">close</span>
                          Rechazar
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
          </div>
        )}

        {vistaActiva === 'perfiles' && (
          <div className="vista-perfiles">
            <div className="page-header search-header">
              <div>
                <h2>Administrar Perfiles</h2>
                <p className="page-subtitle">Gestiona los perfiles genéticos registrados en el banco de datos.</p>
              </div>
              <div className="search-bar">
                <div className="search-input-group">
                  <span className="material-symbols-outlined search-icon">search</span>
                  <input value={searchTerm} onChange={e => { setSearchTerm(e.target.value); setSearchError(''); }} placeholder="Buscar..." />
                </div>
                <div className="search-divider" />
                <select value={searchType} onChange={e => { setSearchType(e.target.value); setSearchError(''); }}>
                  <option value="Todos">Todos</option>
                  <option value="nombre">Nombre</option>
                  <option value="codigo">Código</option>
                </select>
                <button className="btn-search-exec" onClick={() => cargarPerfiles(searchTerm, searchType)}>
                  Buscar
                </button>
              </div>
              {searchError && (
                <div className="search-error-card" key={searchError}>
                  <span>{searchError}</span>
                  <button className="error-close-btn" onClick={() => setSearchError('')}>×</button>
                </div>
              )}
            </div>
            {loading ? (
              <p className="empty-state-text">Cargando perfiles...</p>
            ) : perfilesList.length === 0 ? (
              <div className="empty-state-card">
                <span className="material-symbols-outlined empty-icon">group_off</span>
                <p>No se encontraron perfiles.</p>
              </div>
            ) : (
              <div className="perfiles-grid">
                {perfilesList.map(p => (
                  <div key={p.idPerfil} className={`perfil-card${p.estado !== 1 ? ' inactive' : ''}`}>
                    <div className="perfil-header">
                      <div className="perfil-avatar">
                        <span>{p.nombreCompleto?.charAt(0)?.toUpperCase() || '?'}</span>
                      </div>
                      <div className="perfil-name-block">
                        <h3 className={`perfil-name${p.estado !== 1 ? ' line-through' : ''}`}>{p.nombreCompleto}</h3>
                        <p className="perfil-code">{p.codigoSecuencia}</p>
                      </div>
                      <span className={`status-dot ${p.estado === 1 ? 'active' : 'inactive'}`}>
                        <span className="dot" />
                        {p.estado === 1 ? 'Activo' : 'Inactivo'}
                      </span>
                    </div>
                    <div className="perfil-meta">
                      <div className="meta-item">
                        <span className="meta-label">Fecha de Muestra</span>
                        <span className="meta-value">{p.fechaMuestra || '—'}</span>
                      </div>
                      <div className="meta-item">
                        <span className="meta-label">Descripción</span>
                        <span className="meta-value truncate">{p.descripcion || '—'}</span>
                      </div>
                    </div>
                    <div className="card-footer">
                      <button className="btn-outline" onClick={() => setEditModal({ open: true, perfil: p })}>
                        <span className="material-symbols-outlined">edit</span>
                        Modificar
                      </button>
                      {p.estado === 1 ? (
                        <button className="btn-destructive" onClick={() => handleDarDeBaja(p)}>
                          <span className="material-symbols-outlined">block</span>
                          Dar de baja
                        </button>
                      ) : (
                        <button className="btn-primary-sm" onClick={() => handleRestaurar(p)}>
                          <span className="material-symbols-outlined">settings_backup_restore</span>
                          Restaurar
                        </button>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        )}

        {vistaActiva === 'logs' && (
          <div className="vista-logs">
            <div className="page-header">
              <div>
                <h2>Logs del Sistema</h2>
                <p className="page-subtitle">Historial de actividad del sistema y acciones administrativas.</p>
              </div>
              <button className="btn-icon-refresh" onClick={cargarLogs} title="Actualizar">
                <span className="material-symbols-outlined">refresh</span>
              </button>
            </div>
            <div className="logs-table-container">
              <div className="logs-table-header">
                <div className="col-id">ID</div>
                <div className="col-user">Usuario</div>
                <div className="col-email">Email</div>
                <div className="col-desc">Detalles</div>
                <div className="col-action">Acción</div>
                <div className="col-date">Fecha</div>
              </div>
              <div className="logs-table-body">
                {logsList.length === 0 ? (
                  <div className="empty-row">No hay registros.</div>
                ) : (
                  logsList.map(log => (
                    <div key={log.idRegistro} className={`log-row${log.esAdmin ? ' admin-row' : ''}`}>
                      <div className="col-id mono">#{log.idRegistro}</div>
                      <div className="col-user">
                        <span className="material-symbols-outlined user-icon">{log.esAdmin ? 'admin_panel_settings' : 'person'}</span>
                        <span className={log.esAdmin ? 'admin-name' : ''}>{log.nombreCuenta}</span>
                        {log.esAdmin && <span className="admin-chip">Admin</span>}
                      </div>
                      <div className="col-email">{log.email}</div>
                      <div className="col-desc">{log.descripcion || '—'}</div>
                      <div className="col-action">{log.acciones}</div>
                      <div className="col-date">{log.fecha}</div>
                    </div>
                  ))
                )}
              </div>
            </div>
          </div>
        )}

        {vistaActiva === 'ultimas' && (
          <div className="vista-ultimas">
            <div className="page-header">
              <div>
                <h2>Últimas Solicitudes Resueltas</h2>
                <p className="page-subtitle">Solicitudes aprobadas y rechazadas recientemente.</p>
              </div>
            </div>
            {ultimasList.length === 0 ? (
              <div className="empty-state-card">
                <span className="material-symbols-outlined empty-icon">inbox</span>
                <p>No hay solicitudes resueltas recientemente.</p>
              </div>
            ) : (
              <div className="solicitudes-grid">
                {ultimasList.map(sol => {
                  const tipoLower = sol.tipo?.toLowerCase() || 'registrar';
                  const chipIcon = { registrar: 'add_circle', modificar: 'edit', baja: 'remove_circle', restaurar: 'settings_backup_restore' }[tipoLower] || 'circle';
                  return (
                    <div key={sol.idSolicitud} className="solicitud-card resolved-card">
                      <div className="card-header">
                        <div>
                          <h3 className="card-title">{sol.email}</h3>
                          {sol.codigoSecuencia && <p className="card-ref">ID Ref: {sol.codigoSecuencia}</p>}
                        </div>
                        <span className={`chip-tipo chip-${tipoLower}`}>
                          <span className="material-symbols-outlined chip-icon">{chipIcon}</span>
                          {sol.tipo}
                        </span>
                      </div>
                      <div className="resolved-meta">
                        <span className={`status-dot ${sol.estado === 1 ? 'approved' : 'rejected'}`}>
                          <span className="dot" />
                          {sol.estado === 1 ? 'Aprobada' : 'Rechazada'}
                        </span>
                        <span className="resolved-date">{sol.fecha}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            )}
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
          { name: 'fechaMuestra', label: 'Fecha de Muestra', type: 'date', required: true },
        ]}
        initialValues={editModal.perfil ? {
          nombreCompleto: editModal.perfil.nombreCompleto,
          codigoSecuencia: editModal.perfil.codigoSecuencia,
          descripcion: editModal.perfil.descripcion,
          fechaMuestra: editModal.perfil.fechaMuestra,
        } : {}}
        submitLabel="Guardar Cambios"
        onSubmit={handleEditarSubmit}
      />
    </div>
  );
}

export default AdminDashboard;