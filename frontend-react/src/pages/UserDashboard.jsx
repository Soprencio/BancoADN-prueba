import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { solicitudesService, perfilesService } from '../services/apiService';
import { useToast } from '../contexts/ToastContext';
import ConfirmationModal from '../components/ConfirmationModal';
import FormModal from '../components/FormModal';
import './UserDashboard.css';

const UserDashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { addToast } = useToast();

  const [showSearch, setShowSearch] = useState(false);
  const [perfil, setPerfil] = useState(null);
  const [loadingProfile, setLoadingProfile] = useState(false);
  const [showPerfil, setShowPerfil] = useState(false);
  const [pendingRequests, setPendingRequests] = useState([]);
  const [showConfirmBaja, setShowConfirmBaja] = useState(false);
  const [showConfirmRestaurar, setShowConfirmRestaurar] = useState(false);
  const [showRequestModal, setShowRequestModal] = useState(false);
  const [requestType, setRequestType] = useState('');
  const [formInitialValues, setFormInitialValues] = useState({});
  const [searchTerm, setSearchTerm] = useState('');
  const [searchType, setSearchType] = useState('Todos');
  const [searchResults, setSearchResults] = useState([]);
  const [loadingSearch, setLoadingSearch] = useState(false);

  const cargarPerfiles = async (term, type) => {
    if (!user) return;
    setLoadingSearch(true);
    try {
      let results = await perfilesService.buscarPerfiles(term || '', type || 'Todos', user.email, false);
      // Filter to only active profiles
      results = results.filter(p => p.estado === 1);
      setSearchResults(results);
    } catch (err) {
      addToast('Error al buscar perfiles', 'error');
    } finally {
      setLoadingSearch(false);
    }
  };

  useEffect(() => {
    if (showSearch && user) {
      cargarPerfiles('', 'Todos');
    }
  }, [showSearch, user]);

  const loadPendingRequests = async () => {
    if (!user) return;
    try {
      const allPending = await solicitudesService.getPending(user.email);
      const userPending = allPending.filter(s => s.email === user.email);
      setPendingRequests(userPending);
    } catch (err) {
      addToast('Error al cargar solicitudes pendientes', 'error');
    }
  };

  useEffect(() => {
    if (user) {
      loadPendingRequests();
    }
  }, [user]);

  const hasPending = (tipo) => {
    return pendingRequests.some(p => p.tipo === tipo);
  };

  const handleVerPerfil = async () => {
    setLoadingProfile(true);
    try {
      const p = await perfilesService.getMyProfile(user.email);
      setPerfil(p);
      setShowPerfil(true);
    } catch (err) {
      addToast('Error al cargar perfil', 'error');
    } finally {
      setLoadingProfile(false);
    }
  };

  const handleOpenRegistrar = () => {
    setRequestType('REGISTRAR');
    setFormInitialValues({});
    setShowRequestModal(true);
  };

  const handleOpenModificar = async () => {
    setRequestType('MODIFICAR');
    try {
      const p = await perfilesService.getMyProfile(user.email);
      if (p) {
        setFormInitialValues({
          nombreCompleto: p.nombreCompleto,
          codigoSecuencia: p.codigoSecuencia,
          descripcion: p.descripcion,
          fechaMuestra: p.fechaMuestra,
        });
      } else {
        setFormInitialValues({});
      }
    } catch (err) {
      addToast('Error al cargar perfil para modificar', 'error');
      setFormInitialValues({});
    } finally {
      setShowRequestModal(true);
    }
  };

  const handleRequestSubmit = async (values) => {
    const solicitud = {
      tipo: requestType,
      email: user.email,
      nombreCompleto: values.nombreCompleto || '',
      codigoSecuencia: values.codigoSecuencia || '',
      descripcion: values.descripcion || '',
      fechaMuestra: values.fechaMuestra || '',
    };
    try {
      await solicitudesService.createRequest(solicitud);
      addToast('Solicitud enviada', 'success');
      setShowRequestModal(false);
      await loadPendingRequests();
    } catch (err) {
      addToast('Error al enviar solicitud', 'error');
    }
  };

  const handleConfirmBaja = async () => {
    try {
      await solicitudesService.createRequest({
        tipo: 'BAJA',
        email: user.email,
      });
      addToast('Solicitud de baja enviada', 'success');
      setShowConfirmBaja(false);
      await loadPendingRequests();
    } catch (err) {
      addToast('Error al enviar solicitud de baja', 'error');
    }
  };

  const handleConfirmRestaurar = async () => {
    try {
      await solicitudesService.createRequest({
        tipo: 'RESTAURAR',
        email: user.email,
      });
      addToast('Solicitud de restauración enviada', 'success');
      setShowConfirmRestaurar(false);
      await loadPendingRequests();
    } catch (err) {
      addToast('Error al enviar solicitud de restauración', 'error');
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="user-dashboard">
      <header className="topbar">
        <div className="topbar-left">
          <div className="user-info">
            <span className="user-avatar">
              {user?.nombreCuenta?.charAt(0)?.toUpperCase() || 'U'}
            </span>
            <div className="user-details">
              <span className="user-name">{user?.nombreCuenta || 'Usuario'}</span>
              <span className="user-email">{user?.email}</span>
            </div>
          </div>
        </div>
        <div className="topbar-center">
          <h1>
            <span className="brand-coral">Simple</span>
            <span className="brand-blue">ADN</span>
          </h1>
        </div>
        <div className="topbar-right">
          <button className="btn-icon" onClick={() => setShowSearch(true)} title="Buscar Perfil">
            <span className="material-symbols-outlined">search</span>
            <span className="btn-label">Buscar</span>
          </button>
          <button className="btn-icon btn-logout" onClick={handleLogout} title="Cerrar Sesión">
            <span className="material-symbols-outlined">logout</span>
          </button>
        </div>
      </header>

      <main className="main-content">
        {showSearch ? (
          <div className="vista-perfiles">
            <div className="page-header search-header">
              <div>
                <h2>Buscar Perfiles</h2>
                <p className="page-subtitle">Explorá los perfiles genéticos activos registrados en el banco de datos.</p>
              </div>
              <div className="search-bar">
                <div className="search-input-group">
                  <span className="material-symbols-outlined search-icon">search</span>
                  <input value={searchTerm} onChange={e => setSearchTerm(e.target.value)} placeholder="Buscar..." />
                </div>
                <div className="search-divider" />
                <select value={searchType} onChange={e => setSearchType(e.target.value)}>
                  <option value="Todos">Todos</option>
                  <option value="nombre">Nombre</option>
                  <option value="codigo">Código</option>
                </select>
                <button className="btn-search-exec" onClick={() => cargarPerfiles(searchTerm, searchType)}>
                  Buscar
                </button>
              </div>
            </div>
            {loadingSearch ? (
              <p className="empty-state-text">Cargando perfiles...</p>
            ) : searchResults.length === 0 ? (
              <div className="empty-state-card">
                <span className="material-symbols-outlined empty-icon">group_off</span>
                <p>No se encontraron perfiles activos.</p>
              </div>
            ) : (
              <div className="perfiles-grid">
                {searchResults.map(p => (
                  <div key={p.idPerfil} className="perfil-card">
                    <div className="perfil-header">
                      <div className="perfil-avatar">
                        <span>{p.nombreCompleto?.charAt(0)?.toUpperCase() || '?'}</span>
                      </div>
                      <div className="perfil-name-block">
                        <h3 className="perfil-name">{p.nombreCompleto}</h3>
                        <p className="perfil-code">{p.codigoSecuencia}</p>
                      </div>
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
                  </div>
                ))}
              </div>
            )}
            <button className="btn btn-secondary" style={{ marginTop: '1rem' }} onClick={() => setShowSearch(false)}>
              ← Volver
            </button>
          </div>
        ) : (
          <section className="dashboard-body">
            <div className="left-panel">
              <h3>Mis Solicitudes</h3>
              <div className="btn-group">
                <button className="btn btn-primary" onClick={handleOpenRegistrar}>
                  Solicitar Registro
                  {hasPending('REGISTRAR') && <span className="badge-pill"><span className="material-symbols-outlined">hourglass_empty</span>Pendiente</span>}
                </button>

                <button className="btn btn-primary" onClick={handleOpenModificar}>
                  Solicitar Modificación
                  {hasPending('MODIFICAR') && <span className="badge-pill"><span className="material-symbols-outlined">hourglass_empty</span>Pendiente</span>}
                </button>

                <button className="btn btn-danger" onClick={() => setShowConfirmBaja(true)}>
                  Solicitar Baja
                  {hasPending('BAJA') && <span className="badge-pill"><span className="material-symbols-outlined">hourglass_empty</span>Pendiente</span>}
                </button>

                <button className="btn btn-success" onClick={() => setShowConfirmRestaurar(true)}>
                  Solicitar Restauración
                  {hasPending('RESTAURAR') && <span className="badge-pill"><span className="material-symbols-outlined">hourglass_empty</span>Pendiente</span>}
                </button>
              </div>
            </div>

            <div className="right-panel">
              <h3>Mi Perfil Genético</h3>
              <button className="btn btn-primary" onClick={handleVerPerfil}>
                Consultar mis datos
              </button>

              {showPerfil && (
                loadingProfile ? (
                  <p>Cargando...</p>
                ) : perfil === null ? (
                  <p>No tenés un perfil registrado. Usá "Solicitar Registro".</p>
                ) : perfil.estado === 0 ? (
                  <>
                    <span className="status-chip inactive">Inactivo</span>
                    <p>Tu perfil está dado de baja.</p>
                    <p>Usá "Solicitar Restauración" para reactivarlo.</p>
                  </>
                ) : (
                  <>
                    <span className="status-chip active">Activo</span>
                    <p><strong>Nombre:</strong> {perfil.nombreCompleto}</p>
                    <p><strong>Código:</strong> {perfil.codigoSecuencia}</p>
                    <p><strong>Fecha de muestra:</strong> {perfil.fechaMuestra}</p>
                    <p><strong>Descripción:</strong> {perfil.descripcion}</p>
                  </>
                )
              )}
            </div>
          </section>
        )}
      </main>

      <ConfirmationModal
        isOpen={showConfirmBaja}
        onClose={() => setShowConfirmBaja(false)}
        title="Confirmar Baja"
        message="¿Confirmás que querés dar de baja tu perfil? Quedará oculto hasta que un admin lo apruebe."
        confirmLabel="Enviar Solicitud"
        confirmVariant="danger"
        onConfirm={handleConfirmBaja}
      />

      <ConfirmationModal
        isOpen={showConfirmRestaurar}
        onClose={() => setShowConfirmRestaurar(false)}
        title="Confirmar Restauración"
        message="¿Confirmás que querés restaurar tu perfil?"
        confirmLabel="Enviar Solicitud"
        confirmVariant="success"
        onConfirm={handleConfirmRestaurar}
      />

      <FormModal
        isOpen={showRequestModal}
        onClose={() => setShowRequestModal(false)}
        title={requestType === 'REGISTRAR' ? 'Solicitar Registro de Perfil' : 'Solicitar Modificación de Perfil'}
        fields={[
          { name: 'nombreCompleto', label: 'Nombre Completo', required: true },
          { name: 'codigoSecuencia', label: 'Código de Secuencia', required: true },
          { name: 'descripcion', label: 'Descripción', required: true },
          { name: 'fechaMuestra', label: 'Fecha de Muestra', type: 'date', required: true },
        ]}
        initialValues={formInitialValues}
        submitLabel="Enviar Solicitud"
        onSubmit={handleRequestSubmit}
      />
    </div>
  );
};

export default UserDashboard;
