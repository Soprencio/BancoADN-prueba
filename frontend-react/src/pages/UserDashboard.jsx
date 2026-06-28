import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { solicitudesService, perfilesService } from '../services/mockService';
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
  const [formValues, setFormValues] = useState({
    nombrePerfil: '',
    codigoSecuencia: '',
    descripcion: '',
    fechaMuestra: '',
  });

  // Load pending requests for current user
  const loadPending = async () => {
    if (!user) return;
    const allPending = await solicitudesService.getPending();
    const userPending = allPending.filter((s) => s.idCuenta === user.idCuenta);
    setPendingRequests(userPending);
  };

  useEffect(() => {
    if (user) {
      loadPending();
    }
  }, [user]);

  const hasPending = (type) => {
    return pendingRequests.some((p) => p.tipo === type);
  };

  const handleRequestSubmit = async (values) => {
    const datos = {
      nombreCompleto: values.nombrePerfil,
      codigoSecuencia: values.codigoSecuencia,
      descripcion: values.descripcion,
      fechaMuestra: values.fechaMuestra,
      email: user.email,
      idCuenta: user.idCuenta,
    };
    try {
      await solicitudesService.createRequest(requestType, datos);
      addToast('Solicitud enviada exitosamente', 'success');
      setShowRequestModal(false);
      // reset form
      setFormValues({
        nombrePerfil: '',
        codigoSecuencia: '',
        descripcion: '',
        fechaMuestra: '',
      });
      await loadPending();
    } catch (err) {
      addToast('Error al enviar solicitud', 'error');
    }
  };

  const handlePerfilToggle = () => {
    setShowPerfil(!showPerfil);
    if (showPerfil) {
      setPerfil(null);
      setLoadingProfile(true);
      if (user?.idCuenta) {
        perfilesService.getMyProfile(user.idCuenta).then((p) => {
          setPerfil(p);
          setLoadingProfile(false);
        });
      }
    }
  };

  // Helper to set form defaults based on request type and current profile
  const setFormDefaults = async (type) => {
    setRequestType(type);
    if (type === 'MODIFICAR' && user?.idCuenta) {
      const p = await perfilesService.getMyProfile(user.idCuenta);
      if (p) {
        setFormValues({
          nombrePerfil: p.nombreCompleto,
          codigoSecuencia: p.codigoSecuencia,
          descripcion: p.descripcion,
          fechaMuestra: p.fechaMuestra,
        });
      } else {
        // empty
        setFormValues({
          nombrePerfil: '',
          codigoSecuencia: '',
          descripcion: '',
          fechaMuestra: '',
        });
      }
    } else {
      // REGISTRAR or others: empty
      setFormValues({
        nombrePerfil: '',
        codigoSecuencia: '',
        descripcion: '',
        fechaMuestra: '',
      });
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="dashboard">
      {/* TopBar */}
      <header className="topbar">
        <div className="left">
          <button
            className="btn btn-transparent"
            onClick={() => setShowSearch(true)}
          >
            Buscar Perfil
          </button>
        </div>
        <div className="center">
          <h1>
            <span style={{ color: '#d32f2f' }}>Simple</span>
            <span style={{ color: '#1976d2' }}>ADN</span>
          </h1>
        </div>
        <div className="right">
          <button className="btn btn-link" onClick={handleLogout}>
            Cerrar Sesión
          </button>
        </div>
      </header>

      {/* Main */}
      <main className="main-content">
        {showSearch ? (
          <div className="search-view">
            <h2>Buscar Perfil</h2>
            <p>Contenido de búsqueda de perfiles (pendiente de implementar).</p>
            <button
              className="btn btn-secondary"
              onClick={() => setShowSearch(false)}
            >
              Volver al Panel
            </button>
          </div>
        ) : (
          <>
            <section className="dashboard-body">
              <div className="left-panel">
                <h3>Solicitudes</h3>
                <div className="btn-group">
                  <button
                    className={`btn btn-primary ${hasPending('REGISTRAR') ? 'active' : ''}`}
                    onClick={() => {
                      setFormDefaults('REGISTRAR');
                      setShowRequestModal(true);
                    }}
                  >
                    Solicitar Registro de Perfil
                    {hasPending('REGISTRAR') && (
                      <span className="badge">Pendiente</span>
                    )}
                  </button>
                  <button
                    className={`btn btn-primary ${hasPending('MODIFICAR') ? 'active' : ''}`}
                    onClick={() => {
                      setFormDefaults('MODIFICAR');
                      setShowRequestModal(true);
                    }}
                  >
                    Solicitar Modificación de Perfil
                    {hasPending('MODIFICAR') && (
                      <span className="badge">Pendiente</span>
                    )}
                  </button>
                  <button
                    className={`btn btn-danger ${hasPending('BAJA') ? 'active' : ''}`}
                    onClick={() => {
                      setShowConfirmBaja(true);
                    }}
                  >
                    Solicitar Baja de Perfil
                    {hasPending('BAJA') && (
                      <span className="badge">Pendiente</span>
                    )}
                  </button>
                  <button
                    className={`btn btn-success ${hasPending('RESTAURAR') ? 'active' : ''}`}
                    onClick={() => {
                      setShowConfirmRestaurar(true);
                    }}
                  >
                    Solicitar Restauración de Perfil
                    {hasPending('RESTAURAR') && (
                      <span className="badge">Pendiente</span>
                    )}
                  </button>
                </div>
              </div>

              <div className="right-panel">
                <div className="profile-section">
                  <button
                    className="btn btn-primary"
                    onClick={handlePerfilToggle}
                  >
                    {showPerfil ? 'Ocultar mi Perfil' : 'Ver mi Perfil'}
                  </button>
                  {showPerfil && (
                    <div className="profile-card">
                      {loadingProfile ? (
                        <p>Cargando perfil...</p>
                      ) : perfil ? (
                        <>
                          <p><strong>Nombre:</strong> {perfil.nombreCompleto}</p>
                          <p><strong>Código de Secuencia:</strong> {perfil.codigoSecuencia}</p>
                          <p><strong>Fecha de Muestra:</strong> {perfil.fechaMuestra}</p>
                          <p><strong>Descripción:</strong> {perfil.descripcion}</p>
                          <p>
                            <span
                              className={`status-chip ${
                                perfil.estado === 1 ? 'active' : 'inactive'
                              }`}
                            >
                              {perfil.estado === 1 ? 'Activo' : 'Inactivo'}
                            </span>
                          </p>
                        </>
                      ) : (
                        <p>No tienes un perfil registrado.</p>
                      )}
                    </div>
                  )}
                </div>
              </div>
            </section>
          </>
        )}
      </main>

      {/* Modals */}
      <ConfirmationModal
        isOpen={showConfirmBaja}
        onClose={() => setShowConfirmBaja(false)}
        title="Confirmar Baja"
        message="¿Estás seguro que deseas enviar una solicitud de baja de tu perfil? Esta acción ocultará tu perfil hasta que un administrador la apruebe."
        confirmLabel="Enviar Solicitud"
        confirmVariant="danger"
        onConfirm={() => {
          const datos = {
            nombreCompleto: '',
            codigoSecuencia: '',
            descripcion: '',
            fechaMuestra: '',
            email: user?.email,
            idCuenta: user?.idCuenta,
          };
          solicitudesService.createRequest('BAJA', { ...datos, datosSolicitud: null }).then(() => {
            addToast('Solicitud de baja enviada', 'success');
            setShowConfirmBaja(false);
            loadPending();
          }).catch(() => {
            addToast('Error al enviar solicitud', 'error');
          });
        }}
      />

      <ConfirmationModal
        isOpen={showConfirmRestaurar}
        onClose={() => setShowConfirmRestaurar(false)}
        title="Confirmar Restauración"
        message="¿Estás seguro que deseas enviar una solicitud de restauración de tu perfil? Esta acción reactivará tu perfil si estaba inactivo."
        confirmLabel="Enviar Solicitud"
        confirmVariant="success"
        onConfirm={() => {
          const datos = {
            nombreCompleto: '',
            codigoSecuencia: '',
            descripcion: '',
            fechaMuestra: '',
            email: user?.email,
            idCuenta: user?.idCuenta,
          };
          solicitudesService.createRequest('RESTAURAR', { ...datos, datosSolicitud: null }).then(() => {
            addToast('Solicitud de restauración enviada', 'success');
            setShowConfirmRestaurar(false);
            loadPending();
          }).catch(() => {
            addToast('Error al enviar solicitud', 'error');
          });
        }}
      />

      <FormModal
        isOpen={showRequestModal}
        onClose={() => setShowRequestModal(false)}
        title={requestType === 'REGISTRAR' ? 'Solicitar Registro de Perfil' : 'Solicitar Modificación de Perfil'}
        fields={[
          { name: 'nombrePerfil', label: 'Nombre del Perfil', required: true },
          { name: 'codigoSecuencia', label: 'Código de Secuencia', required: true, pattern: '^[0-9]+$' },
          { name: 'descripcion', label: 'Descripción', required: true },
          { name: 'fechaMuestra', label: 'Fecha de Muestra (YYYY-MM-DD)', required: true, type: 'date' },
        ]}
        submitLabel="Enviar Solicitud"
        onSubmit={handleRequestSubmit}
      />
    </div>
  );
};

export default UserDashboard;