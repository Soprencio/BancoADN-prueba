import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import {
  solicitudesService,
  perfilesService,
  logsService,
} from '../services/mockService';
import { useToast } from '../contexts/ToastContext';
import ConfirmationModal from '../components/ConfirmationModal';
import FormModal from '../components/FormModal';
import './AdminDashboard.css';

const AdminDashboard = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const { addToast } = useToast();

  // Tabs
  const [tab, setTab] = useState('pendientes'); // pendientes, perfiles, logs, recientes
  const [activeTabIndex, setActiveTabIndex] = useState(0);
  const tabGroupRef = useRef(null);
  const tabBtnRefs = useRef([]);

  // Data
  const [pending, setPending] = useState([]);
  const [perfiles, setPerfiles] = useState([]);
  const [logs, setLogs] = useState([]);
  const [recientes, setRecientes] = useState([]);
  const [loading, setLoading] = useState(false);

  // Modals
  const [showConfirmApprove, setShowConfirmApprove] = useState(false);
  const [showConfirmReject, setShowConfirmReject] = useState(false);
  const [approveId, setApproveId] = useState(null);
  const [rejectId, setRejectId] = useState(null);
  const [showEditPerfilModal, setShowEditPerfilModal] = useState(false);
  const [editPerfilId, setEditPerfilId] = useState(null);
  const [editPerfilValues, setEditPerfilValues] = useState({
    nombreCompleto: '',
    codigoSecuencia: '',
    descripcion: '',
    fechaMuestra: '',
    estado: 1,
  });
  const [showConfirmBaja, setShowConfirmBaja] = useState(false);
  const [showConfirmRestaurar, setShowConfirmRestaurar] = useState(false);
  const [bajaId, setBajaId] = useState(null);
  const [restaurarId, setRestaurarId] = useState(null);

  // Load data
  const loadAll = async () => {
    setLoading(true);
    try {
      const [pendingRes, perfilesRes, logsRes, recientesRes] = await Promise.all([
        solicitudesService.getPending(),
        perfilesService.getAll(),
        logsService.getLogs(),
        solicitudesService.getRecent(),
      ]);
      setPending(pendingRes);
      setPerfiles(perfilesRes);
      setLogs(logsRes);
      setRecientes(recientesRes);
    } catch (err) {
      addToast('Error al cargar datos', 'error');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (user) {
      loadAll();
    }
  }, [user]);

  const refresh = () => {
    loadAll();
  };

  // Approve request
  const handleApprove = async (id) => {
    setApproveId(id);
    setShowConfirmApprove(true);
  };

  const confirmApprove = async () => {
    if (!approveId) return;
    try {
      await solicitudesService.approveRequest(approveId, user?.email || '');
      addToast('Solicitud aprobada', 'success');
      setShowConfirmApprove(false);
      setApproveId(null);
      // refresh data
      loadAll();
    } catch (err) {
      addToast('Error al aprobar', 'error');
    }
  };

  // Reject request
  const handleReject = async (id) => {
    setRejectId(id);
    setShowConfirmReject(true);
  };

  const confirmReject = async () => {
    if (!rejectId) return;
    try {
      await solicitudesService.rejectRequest(rejectId, user?.email || '');
      addToast('Solicitud rechazada', 'success');
      setShowConfirmReject(false);
      setRejectId(null);
      loadAll();
    } catch (err) {
      addToast('Error al rechazar', 'error');
    }
  };

  // Baja perfil (admin)
  const handleConfirmBaja = (id) => {
    setBajaId(id);
    setShowConfirmBaja(true);
  };

  const confirmBaja = async () => {
    if (!bajaId) return;
    try {
      await perfilesService.deactivateProfile(bajaId);
      addToast('Perfil dado de baja', 'success');
      setShowConfirmBaja(false);
      setBajaId(null);
      // log
      await logsService.addLog(user?.email || '', 3, '');
      loadAll();
    } catch (err) {
      addToast('Error al dar de baja', 'error');
    }
  };

  // Restaurar perfil (admin)
  const handleConfirmRestaurar = (id) => {
    setRestaurarId(id);
    setShowConfirmRestaurar(true);
  };

  const confirmRestaurar = async () => {
    if (!restaurarId) return;
    try {
      await perfilesService.reactivateProfile(restaurarId);
      addToast('Perfil restaurado', 'success');
      setShowConfirmRestaurar(false);
      setRestaurarId(null);
      // log
      await logsService.addLog(user?.email || '', 4, '');
      loadAll();
    } catch (err) {
      addToast('Error al restaurar', 'error');
    }
  };

  // Edit perfil (admin direct edit)
  const handleEditPerfil = (id) => {
    setEditPerfilId(id);
    const perf = perfiles.find((p) => p.idPerfil === id);
    if (perf) {
      setEditPerfilValues({
        nombreCompleto: perf.nombreCompleto,
        codigoSecuencia: perf.codigoSecuencia,
        descripcion: perf.descripcion,
        fechaMuestra: perf.fechaMuestra,
        estado: perf.estado,
      });
    } else {
      // reset
      setEditPerfilValues({
        nombreCompleto: '',
        codigoSecuencia: '',
        descripcion: '',
        fechaMuestra: '',
        estado: 1,
      });
    }
    setShowEditPerfilModal(true);
  };

  const handleEditPerfilSubmit = async (values) => {
    if (!editPerfilId) return;
    try {
      await perfilesService.updateProfile(editPerfilId, {
        nombreCompleto: values.nombreCompleto,
        codigoSecuencia: values.codigoSecuencia,
        descripcion: values.descripcion,
        fechaMuestra: values.fechaMuestra,
        estado: Number(values.estado),
      });
      addToast('Perfil actualizado', 'success');
      setShowEditPerfilModal(false);
      // log
      await logsService.addLog(user?.email || '', 5, ` modifico perfil de: ${values.nombreCompleto}`);
      loadAll();
    } catch (err) {
      addToast('Error al actualizar perfil', 'error');
    }
  };

  // Update indicator position when active tab changes
  useEffect(() => {
    if (!tabGroupRef.current || !tabBtnRefs.current[activeTabIndex]) return;
    const btn = tabBtnRefs.current[activeTabIndex];
    const container = tabGroupRef.current;
    const btnRect = btn.getBoundingClientRect();
    const containerRect = container.getBoundingClientRect();
    const left = btnRect.left - containerRect.left;
    const width = btnRect.width;
    const indicator = container.querySelector('.tab-indicator');
    if (indicator) {
      indicator.style.left = `${left}px`;
      indicator.style.width = `${width}px`;
    }
  }, [activeTabIndex]);

  // Handle tab click
  const handleTabClick = (index, tabName) => {
    setTab(tabName);
    setActiveTabIndex(index);
  };

  // Render tab content
  const renderTabContent = () => {
    switch (tab) {
      case 'pendientes':
        return (
          <div className="tab-content">
            <h2>Solicitudes Pendientes</h2>
            {pending.length === 0 ? (
              <p>No hay solicitudes pendientes.</p>
            ) : (
              <div className="request-list">
                {pending.map((req) => (
                  <div key={req.idSolicitud} className="request-card">
                    <div className="request-header">
                      <div className="request-info">
                        <strong>Solicitud #{req.idSolicitud}</strong>
                        <br />
                        <span>
                          Tipo:{" "}
                          {req.tipo === 'REGISTRAR'
                            ? 'Registro'
                            : req.tipo === 'MODIFICAR'
                            ? 'Modificación'
                            : req.tipo === 'BAJA'
                            ? 'Baja'
                            : 'Restaurar'}
                        </span>
                        <br />
                        <small>
                          Usuario: {req.datosSolicitud?.email ||
                            'desconocido'}
                        </small>
                      </div>
                      <div className="request-actions">
                        {req.tipo === 'REGISTRAR' || req.tipo === 'MODIFICAR' ? (
                          <>
                            <button
                              className="btn btn-sm btn-success"
                              onClick={() => handleApprove(req.idSolicitud)}
                            >
                              Aceptar
                            </button>
                            <button
                              className="btn btn-sm btn-danger ms-2"
                              onClick={() => handleReject(req.idSolicitud)}
                            >
                              Rechazar
                            </button>
                          </>
                        ) : (
                          <>
                            <button
                              className="btn btn-sm btn-success"
                              onClick={() => handleApprove(req.idSolicitud)}
                            >
                              Aceptar
                            </button>
                            <button
                              className="btn btn-sm btn-danger ms-2"
                              onClick={() => handleReject(req.idSolicitud)}
                            >
                              Rechazar
                            </button>
                          </>
                        )}
                      </div>
                    </div>
                    {(req.tipo === 'REGISTRAR' || req.tipo === 'MODIFICAR') && req.datosSolicitud && (
                      <div className="request-details">
                        <p>
                          <strong>Nombre:</strong> {req.datosSolicitud?.nombreCompleto}
                        </p>
                        <p>
                          <strong>Código:</strong> {req.datosSolicitud?.codigoSecuencia}
                        </p>
                        <p>
                          <strong>Fecha:</strong> {req.datosSolicitud?.fechaMuestra}
                        </p>
                        <p>
                          <strong>Descripción:</strong> {req.datosSolicitud?.descripcion}
                        </p>
                      </div>
                    )}
                    <div className="request-badge">
                      {req.estado === 2 ? (
                        <span className="badge badge-rejected">Rechazada</span>
                      ) : null}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        );
      case 'perfiles':
        return (
          <div className="tab-content">
            <h2>Administrar Perfiles</h2>
            {perfiles.length === 0 ? (
              <p>No hay perfiles registrados.</p>
            ) : (
              <div className="profiles-list">
                {perfiles.map((perf) => (
                  <div key={perf.idPerfil} className="profile-card">
                    <div className="profile-header">
                      <div>
                        <strong>{perf.nombreCompleto}</strong>
                        <br />
                        <small>Código: {perf.codigoSecuencia}</small>
                      </div>
                      <div className="profile-actions">
                        <button
                          className="btn btn-sm btn-secondary"
                          onClick={() => handleEditPerfil(perf.idPerfil)}
                        >
                          Modificar
                        </button>
                        {perf.estado === 1 ? (
                          <button
                            className="btn btn-sm btn-danger"
                            onClick={() => handleConfirmBaja(perf.idPerfil)}
                          >
                            Dar de Baja
                          </button>
                        ) : (
                          <button
                            className="btn btn-sm btn-success"
                            onClick={() => handleConfirmRestaurar(perf.idPerfil)}
                          >
                            Restaurar
                          </button>
                        )}
                      </div>
                    </div>
                    <div className="profile-details">
                      <p>
                        <strong>Fecha de Muestra:</strong> {perf.fechaMuestra}
                      </p>
                      <p>
                        <strong>Descripción:</strong> {perf.descripcion}
                      </p>
                      <p>
                        <span
                          className={`status-chip ${
                            perf.estado === 1 ? 'active' : 'inactive'
                          }`}
                        >
                          {perf.estado === 1 ? 'Activo' : 'Inactivo'}
                        </span>
                      </p>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        );
      case 'logs':
        return (
          <div className="tab-content">
            <h2>Logs (últimos 30)</h2>
            {logs.length === 0 ? (
              <p>No hay registros de logs.</p>
            ) : (
              <div className="logs-list">
                {logs.map((log) => (
                  <div key={log.idRegistro} className="log-item">
                    <strong>#{log.idRegistro}</strong> -{' '}
                    {log.fecha} -{' '}
                    {log.detalle}
                  </div>
                ))}
              </div>
            )}
          </div>
        );
      case 'recientes':
        return (
          <div className="tab-content">
            <h2>Últimas Solicitudes (resueltas)</h2>
            {recientes.length === 0 ? (
              <p>No hay solicitudes resueltas recientemente.</p>
            ) : (
              <div className="recent-list">
                {recientes.map((req) => (
                  <div key={req.idSolicitud} className="request-card">
                    <div className="request-header">
                      <div className="request-info">
                        <strong>Solicitud #{req.idSolicitud}</strong>
                        <br />
                        <span>
                          Tipo:{" "}
                          {req.tipo === 'REGISTRAR'
                            ? 'Registro'
                            : req.tipo === 'MODIFICAR'
                            ? 'Modificación'
                            : req.tipo === 'BAJA'
                            ? 'Baja'
                            : 'Restaurar'}
                        </span>
                        <br />
                        <small>
                          Usuario: {req.datosSolicitud?.email ||
                            'desconocido'}
                        </small>
                      </div>
                      <div className="request-actions">
                        <span>
                          Resuelta por: {req.idCuentaAdmin ?? 'desconocido'}
                        </span>
                      </div>
                    </div>
                    {(req.tipo === 'REGISTRAR' || req.tipo === 'MODIFICAR') && req.datosSolicitud && (
                      <div className="request-details">
                        <p>
                          <strong>Nombre:</strong> {req.datosSolicitud?.nombreCompleto}
                        </p>
                        <p>
                          <strong>Código:</strong> {req.datosSolicitud?.codigoSecuencia}
                        </p>
                        <p>
                          <strong>Fecha:</strong> {req.datosSolicitud?.fechaMuestra}
                        </p>
                        <p>
                          <strong>Descripción:</strong> {req.datosSolicitud?.descripcion}
                        </p>
                      </div>
                    )}
                    <div className="request-badge">
                      {req.estado === 2 ? (
                        <span className="badge badge-rejected">Rechazada</span>
                      ) : null}
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="admin-dashboard">
      <header className="topbar">
        <div className="left">
          <div className="user-info">
            <span className="user-avatar">{user?.nombreCuenta?.charAt(0)?.toUpperCase()}</span>
            <span className="user-email">{user?.email}</span>
          </div>
        </div>
        <div className="center">
          <h1>
            <span style={{ color: '#d32f2f' }}>Simple</span>
            <span style={{ color: '#1976d2' }}>ADN</span>
          </h1>
        </div>
        <div className="right">
          <div className="tab-group" ref={tabGroupRef}>
            <button
              className="tab-btn"
              ref={(el) => (tabBtnRefs.current[0] = el)}
              onClick={() => handleTabClick(0, 'pendientes')}
            >
              Solicitudes
            </button>
            <button
              className="tab-btn"
              ref={(el) => (tabBtnRefs.current[1] = el)}
              onClick={() => handleTabClick(1, 'perfiles')}
            >
              Perfil
            </button>
            <button
              className="tab-btn"
              ref={(el) => (tabBtnRefs.current[2] = el)}
              onClick={() => handleTabClick(2, 'logs')}
            >
              Logs
            </button>
            <button
              className="tab-btn"
              ref={(el) => (tabBtnRefs.current[3] = el)}
              onClick={() => handleTabClick(3, 'recientes')}
            >
              Ult. Solicitudes
            </button>
            <div className="tab-indicator" />
          </div>
          <button className="btn btn-link" onClick={logout}>
            Cerrar Sesión
          </button>
        </div>
      </header>

      <main className="main-content">
        {renderTabContent()}
      </main>

      {/* Modals */}
      <ConfirmationModal
        isOpen={showConfirmApprove}
        onClose={() => {
          setShowConfirmApprove(false);
          setApproveId(null);
        }}
        title="Confirmar Aprobación"
        message="¿Estás seguro de aprobar esta solicitud? Esta acción será irreversible."
        confirmLabel="Aprobar"
        confirmVariant="success"
        onConfirm={confirmApprove}
      />

      <ConfirmationModal
        isOpen={showConfirmReject}
        onClose={() => {
          setShowConfirmReject(false);
          setRejectId(null);
        }}
        title="Confirmar Rechazo"
        message="¿Estás seguro de rechazar esta solicitud? Esta acción será irreversible."
        confirmLabel="Rechazar"
        confirmVariant="danger"
        onConfirm={confirmReject}
      />

      <ConfirmationModal
        isOpen={showConfirmBaja}
        onClose={() => {
          setShowConfirmBaja(false);
          setBajaId(null);
        }}
        title="Confirmar Baja de Perfil"
        message="¿Estás seguro de dar de baja este perfil? Esta acción lo marcará como inactivo."
        confirmLabel="Dar de Baja"
        confirmVariant="danger"
        onConfirm={confirmBaja}
      />

      <ConfirmationModal
        isOpen={showConfirmRestaurar}
        onClose={() => {
          setShowConfirmRestaurar(false);
          setRestaurarId(null);
        }}
        title="Confirmar Restauración de Perfil"
        message="¿Estás seguro de restaurar este perfil? Esta acción lo marcará como activo."
        confirmLabel="Restaurar"
        confirmVariant="success"
        onConfirm={confirmRestaurar}
      />

      <FormModal
        isOpen={showEditPerfilModal}
        onClose={() => {
          setShowEditPerfilModal(false);
          setEditPerfilId(null);
          setEditPerfilValues({
            nombreCompleto: '',
            codigoSecuencia: '',
            descripcion: '',
            fechaMuestra: '',
            estado: 1,
          });
        }}
        title="Modificar Perfil"
        fields={[
          { name: 'nombreCompleto', label: 'Nombre Completo', required: true },
          { name: 'codigoSecuencia', label: 'Código de Secuencia', required: true },
          { name: 'descripcion', label: 'Descripción', required: true },
          { name: 'fechaMuestra', label: 'Fecha de Muestra', required: true, type: 'date' },
          { name: 'estado', label: 'Estado', required: true, type: 'select', options: [
            { label: 'Activo', value: 1 },
            { label: 'Inactivo', value: 0 },
          ] }
        ]}
        submitLabel="Guardar Cambios"
        onSubmit={handleEditPerfilSubmit}
      />
    </div>
  );
};

export default AdminDashboard;