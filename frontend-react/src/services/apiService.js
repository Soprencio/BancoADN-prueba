// API Service layer for BancoADN frontend
// Connects to backend at http://localhost:7000/api

const API_BASE = 'http://localhost:7000/api';

/**
 * Get headers for requests
 * @param {boolean} isAdmin - Whether to use admin header
 * @param {string} email - Email for user or admin header
 * @returns {Object} Headers object
 */
function getHeaders(isAdmin, email) {
  const headers = {
    'Content-Type': 'application/json',
  };
  if (email) {
    if (isAdmin) {
      headers['X-Admin-Email'] = email;
    } else {
      headers['X-User-Email'] = email;
    }
  }
  return headers;
}

/**
 * Handle HTTP response
 * @param {Response} response - Fetch response
 * @returns {Promise<any>} Parsed JSON or throws error
 */
async function handleResponse(response) {
  if (!response.ok) {
    let errorMsg = `HTTP ${response.status}`;
    try {
      const errorData = await response.json();
      if (errorData.message) {
        errorMsg = errorData.message;
      }
    } catch (e) {
      // Ignore if not JSON
    }
    throw new Error(errorMsg);
  }
  return response.json();
}

// Auth Service
const authService = {
  /**
   * Login user
   * @param {string} email
   * @param {string} password
   * @returns {Promise<{success: boolean, user: Object|null, message?: string}>}
   */
  login: async (email, password) => {
    try {
      const response = await fetch(`${API_BASE}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ email, password }),
      });
      const data = await handleResponse(response);
      // Expecting: { idCuenta, nombreCuenta, email, idRol }
      return {
        success: true,
        user: {
          idCuenta: data.idCuenta,
          nombreCuenta: data.nombreCuenta,
          email: data.email,
          idRol: data.idRol,
        },
      };
    } catch (error) {
      return { success: false, message: error.message };
    }
  },

  /**
   * Register new user
   * @param {string} email
   * @param {string} password
   * @param {string} nombreCuenta
   * @returns {Promise<{success: boolean, user: Object|null, message?: string}>}
   */
  register: async (email, password, nombreCuenta) => {
    try {
      const response = await fetch(`${API_BASE}/auth/crear-cuenta`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ nombreCuenta, email, password }),
      });
      const data = await handleResponse(response);
      // Expecting: { idCuenta, nombreCuenta, email, idRol } or at least message
      // If we get user data, use it; otherwise construct from request
      const user = data.idCuenta
        ? {
            idCuenta: data.idCuenta,
            nombreCuenta: data.nombreCuenta,
            email: data.email,
            idRol: data.idRol,
          }
        : {
            idCuenta: -1, // placeholder if not returned
            nombreCuenta,
            email,
            idRol: 1, // default role
          };
      return { success: true, user };
    } catch (error) {
      return { success: false, message: error.message };
    }
  },
};

// Cuentas Service
const cuentasService = {
  /**
   * Get all accounts
   * @returns {Promise<Array>}
   */
  getAll: async () => {
    try {
      const response = await fetch(`${API_BASE}/cuentas`, {
        method: 'GET',
      });
      return await handleResponse(response);
    } catch (error) {
      // If endpoint doesn't exist, return empty array for compatibility
      console.warn('Cuentas endpoint not available:', error.message);
      return [];
    }
  },
};

// Perfiles Service
const perfilesService = {
  /**
   * Get all profiles
   * @returns {Promise<Array>}
   */
  getAll: async () => {
    try {
      const response = await fetch(`${API_BASE}/perfiles`, {
        method: 'GET',
      });
      return await handleResponse(response);
    } catch (error) {
      console.warn('Perfiles endpoint not available:', error.message);
      return [];
    }
  },

  /**
   * Get profile by ID
   * @param {number} id
   * @returns {Promise<Object|null>}
   */
  getById: async (id) => {
    try {
      const response = await fetch(`${API_BASE}/perfiles/${id}`, {
        method: 'GET',
      });
      if (!response.ok) {
        if (response.status === 404) return null;
        throw new Error(`HTTP ${response.status}`);
      }
      return await handleResponse(response);
    } catch (error) {
      console.error('Error fetching profile by ID:', error.message);
      return null;
    }
  },

  /**
   * Get profile by email (via cuenta)
   * @param {string} email
   * @returns {Promise<Object|null>}
   */
  getByEmail: async (email) => {
    try {
      // First get cuentas to find idCuenta
      const cuentasResponse = await fetch(`${API_BASE}/cuentas`);
      if (!cuentasResponse.ok) {
        throw new Error('Failed to fetch cuentas');
      }
      const cuentas = await cuentasResponse.json();
      const cuenta = cuentas.find((c) => c.email === email);
      if (!cuenta) return null;

      // Then get perfil by idCuenta (assuming endpoint exists)
      const perfilResponse = await fetch(`${API_BASE}/perfiles/${cuenta.idCuenta}`);
      if (!perfilResponse.ok) {
        if (perfilResponse.status === 404) return null;
        throw new Error(`HTTP ${perfilResponse.status}`);
      }
      return await perfilResponse.json();
    } catch (error) {
      console.error('Error fetching profile by email:', error.message);
      return null;
    }
  },

  /**
   * Get profile of authenticated user
   * @param {string} email - User email for header
   * @returns {Promise<Object|null>}
   */
  getMyProfile: async (email) => {
    try {
      const response = await fetch(`${API_BASE}/perfiles/me`, {
        method: 'GET',
        headers: getHeaders(false, email),
      });
      if (!response.ok) {
        if (response.status === 404) return null;
        throw new Error(`HTTP ${response.status}`);
      }
      return await handleResponse(response);
    } catch (error) {
      console.error('Error fetching my profile:', error.message);
      return null;
    }
  },

  /**
   * Update profile (admin only)
   * @param {number} id
   * @param {Object} data - { nombreCompleto, codigoSecuencia, fechaMuestra, descripcion, estado }
   * @param {string} adminEmail
   * @returns {Promise<Object>}
   */
  updateProfile: async (id, data, adminEmail) => {
    try {
      const response = await fetch(`${API_BASE}/perfiles/${id}/modificar`, {
        method: 'POST',
        headers: getHeaders(true, adminEmail),
        body: JSON.stringify(data),
      });
      return await handleResponse(response);
    } catch (error) {
      throw new Error(`Failed to update profile: ${error.message}`);
    }
  },

  /**
   * Deactivate profile (admin only)
   * @param {number} id
   * @param {string} adminEmail
   * @returns {Promise<Object>}
   */
  deactivateProfile: async (id, adminEmail) => {
    try {
      const response = await fetch(`${API_BASE}/perfiles/${id}/baja`, {
        method: 'POST',
        headers: getHeaders(true, adminEmail),
      });
      return await handleResponse(response);
    } catch (error) {
      throw new Error(`Failed to deactivate profile: ${error.message}`);
    }
  },

  /**
   * Reactivate profile (admin only)
   * @param {number} id
   * @param {string} adminEmail
   * @returns {Promise<Object>}
   */
  reactivateProfile: async (id, adminEmail) => {
    try {
      const response = await fetch(`${API_BASE}/perfiles/${id}/restaurar`, {
        method: 'POST',
        headers: getHeaders(true, adminEmail),
      });
      return await handleResponse(response);
    } catch (error) {
      throw new Error(`Failed to reactivate profile: ${error.message}`);
    }
  },

  /**
   * Search profiles
   * @param {string} term
   * @param {string} type - 'ID', 'Nombre', or 'Todos'
   * @param {string} userEmail
   * @returns {Promise<Array>}
   */
  buscarPerfiles: async (term, type, userEmail) => {
    try {
      const response = await fetch(
        `${API_BASE}/perfiles/buscar?tipo=${type}&texto=${encodeURIComponent(term)}`,
        {
          method: 'GET',
          headers: getHeaders(false, userEmail),
        }
      );
      return await handleResponse(response);
    } catch (error) {
      console.error('Error searching profiles:', error.message);
      return [];
    }
  },
};

// Solicitudes Service
const solicitudesService = {
  /**
   * Get all solicitudes
   * @param {string} userEmail
   * @returns {Promise<Array>}
   */
  getAll: async (userEmail) => {
    try {
      const response = await fetch(`${API_BASE}/solicitudes`, {
        method: 'GET',
        headers: getHeaders(false, userEmail),
      });
      return await handleResponse(response);
    } catch (error) {
      console.error('Error fetching all solicitudes:', error.message);
      return [];
    }
  },

  /**
   * Get pending solicitudes
   * @param {string} userEmail
   * @returns {Promise<Array>}
   */
  getPending: async (userEmail) => {
    try {
      const response = await fetch(`${API_BASE}/solicitudes/pendientes`, {
        method: 'GET',
        headers: getHeaders(false, userEmail),
      });
      return await handleResponse(response);
    } catch (error) {
      console.error('Error fetching pending solicitudes:', error.message);
      return [];
    }
  },

  /**
   * Get recent resolved solicitudes
   * @param {string} userEmail
   * @returns {Promise<Array>}
   */
  getRecent: async (userEmail) => {
    try {
      const response = await fetch(`${API_BASE}/solicitudes/ultimas`, {
        method: 'GET',
        headers: getHeaders(false, userEmail),
      });
      return await handleResponse(response);
    } catch (error) {
      console.error('Error fetching recent solicitudes:', error.message);
      return [];
    }
  },

  /**
   * Create a new solicitud
   * @param {Object} solicitud - { tipo, nombreCompleto, codigoSecuencia, fechaMuestra, descripcion, email }
   * @returns {Promise<Object>}
   */
  createRequest: async (solicitud) => {
    const tipo = solicitud.tipo;
    const nombreCompleto = solicitud.nombreCompleto;
    const codigoSecuencia = solicitud.codigoSecuencia;
    const fechaMuestra = solicitud.fechaMuestra;
    const descripcion = solicitud.descripcion;
    const email = solicitud.email;
    let endpoint = '';
    switch (tipo) {
      case 'REGISTRAR':
        endpoint = '/solicitudes/registrar';
        break;
      case 'MODIFICAR':
        endpoint = '/solicitudes/modificar';
        break;
      case 'BAJA':
        endpoint = '/solicitudes/baja';
        break;
      case 'RESTAURAR':
        endpoint = '/solicitudes/restaurar';
        break;
      default:
        throw new Error(`Invalid solicitud type: ${tipo}`);
    }

    try {
      const response = await fetch(`${API_BASE}${endpoint}`, {
        method: 'POST',
        headers: getHeaders(false, email),
        body: JSON.stringify({
          nombreCompleto,
          codigoSecuencia,
          fechaMuestra,
          descripcion,
          email,
        }),
      });
      const data = await handleResponse(response);
      // Return solicitud object with server-generated id and estado=0 (pending)
      return {
        idSolicitud: data.idSolicitud || Date.now(), // fallback if not returned
        tipo,
        estado: 0,
        fechaSolicitud: new Date().toISOString().split('T')[0],
        idCuentaAdmin: null,
        email,
        nombreCompleto,
        codigoSecuencia,
        fechaMuestra,
        descripcion,
      };
    } catch (error) {
      throw new Error(`Failed to create solicitud: ${error.message}`);
    }
  },

  /**
   * Approve a solicitud (admin only)
   * @param {number} id
   * @param {string} adminEmail
   * @returns {Promise<Object>}
   */
  approveRequest: async (id, adminEmail) => {
    try {
      const response = await fetch(`${API_BASE}/solicitudes/${id}/aprobar`, {
        method: 'POST',
        headers: getHeaders(true, adminEmail),
      });
      const data = await handleResponse(response);
      // Return updated solicitud object (approximate)
      return {
        idSolicitud: id,
        estado: 1, // approved
        idCuentaAdmin: data.idCuentaAdmin || null,
        // other fields would need to be fetched; returning minimal for now
        ...(data.solicitud || {}),
      };
    } catch (error) {
      throw new Error(`Failed to approve solicitud: ${error.message}`);
    }
  },

  /**
   * Reject a solicitud (admin only)
   * @param {number} id
   * @param {string} adminEmail
   * @returns {Promise<Object>}
   */
  rejectRequest: async (id, adminEmail) => {
    try {
      const response = await fetch(`${API_BASE}/solicitudes/${id}/rechazar`, {
        method: 'POST',
        headers: getHeaders(true, adminEmail),
      });
      const data = await handleResponse(response);
      return {
        idSolicitud: id,
        estado: 2, // rejected
        idCuentaAdmin: data.idCuentaAdmin || null,
        ...(data.solicitud || {}),
      };
    } catch (error) {
      throw new Error(`Failed to reject solicitud: ${error.message}`);
    }
  },
};

// Logs Service
const logsService = {
  /**
   * Get all logs
   * @returns {Promise<Array>}
   */
  getLogs: async () => {
    try {
      const response = await fetch(`${API_BASE}/logs`, {
        method: 'GET',
      });
      return await handleResponse(response);
    } catch (error) {
      console.error('Error fetching logs:', error.message);
      return [];
    }
  },

  /**
   * Add a log entry
   * @param {string} email
   * @param {number} idTipoAccion
   * @param {string} detalle
   * @returns {Promise<{idRegistro: number}>}
   */
  addLog: async () => {},
};

export {
  authService,
  cuentasService,
  perfilesService,
  solicitudesService,
  logsService,
};