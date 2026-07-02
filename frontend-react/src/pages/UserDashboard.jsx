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
  const [searchType, setSearchType] = useState('nombre');
  const [searchResults, setSearchResults] = useState([]);

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

  const handleBuscar = async () => {
    try {
      const results = await perfilesService.buscarPerfiles(
        searchTerm,
        searchType,
        user.idRol,
        user.email
      );
      setSearchResults(results);
    } catch (err) {
      addToast('Error al buscar perfiles', 'error');
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
          <span className="user-avatar">&#128100;</span>
          {user?.nombreCuenta} — {user?.email}
        </div>
        <div className="topbar-center">
          <h1>
            <span style={{ color: '#e25c6b' }}>Simple</span>
            <span style={{ color: '#2b94c8' }}>ADN</span>
          </h1>
        </div>
        <div className="topbar-right">
          <button onClick={() => setShowSearch(true)}>🔍 Buscar Perfil</button>
          <button onClick={handleLogout}>Cerrar Sesión</button>
        </div>
      </header>

      <main className="main-content">
        {showSearch ? (
          <div className="search-view">
            <button onClick={() => setShowSearch(false)}>← Volver</button>
            <h2>Buscar Perfiles</h2>
            <div className="search-bar">
              <select value={searchType} onChange={e => setSearchType(e.target.value)}>
                <option value="nombre">Por Nombre</option>
                <option value="codigo">Por Código</option>
              </select>
              <input
                value={searchTerm}
                onChange={e => setSearchTerm(e.target.value)}
                placeholder="Buscar..."
              />
              <button onClick={handleBuscar}>Buscar</button>
            </div>
            {searchResults.length === 0 ? (
              <p>No se encontraron resultados.</p>
            ) : (
              searchResults.map(p => (
                <div key={p.idPerfil} className="profile-card">
                  <p><strong>{p.nombreCompleto}</strong></p>
                  <p>Código: {p.codigoSecuencia}</p>
                  <p>Fecha: {p.fechaMuestra}</p>
                  <p>{p.descripcion}</p>
                </div>
              ))
            )}
          </div>
        ) : (
          <section className="dashboard-body">
            <div className="left-panel">
              <h3>Mis Solicitudes</h3>
              <div className="btn-group">
                <button className="btn btn-primary" onClick={handleOpenRegistrar}>
                  Solicitar Registro
                  {hasPending('REGISTRAR') && <span className="badge">Pendiente</span>}
                </button>

                <button className="btn btn-primary" onClick={handleOpenModificar}>
                  Solicitar Modificación
                  {hasPending('MODIFICAR') && <span className="badge">Pendiente</span>}
                </button>

                <button className="btn btn-danger" onClick={() => setShowConfirmBaja(true)}>
                  Solicitar Baja
                  {hasPending('BAJA') && <span className="badge">Pendiente</span>}
                </button>

                <button className="btn btn-success" onClick={() => setShowConfirmRestaurar(true)}>
                  Solicitar Restauración
                  {hasPending('RESTAURAR') && <span className="badge">Pendiente</span>}
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
