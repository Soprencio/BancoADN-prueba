const accounts = [
  {
    idCuenta: 1,
    email: 'admin@example.com',
    password: 'admin', // not used in mock
    nombreCuenta: 'Administrador',
    idRol: 2,
  },
  {
    idCuenta: 2,
    email: 'juan@example.com',
    password: 'juan',
    nombreCuenta: 'Juan Pérez',
    idRol: 1,
  },
  {
    idCuenta: 3,
    email: 'maria@example.com',
    password: 'maria',
    nombreCuenta: 'María Gómez',
    idRol: 1,
  },
];

let perfiles = [
  {
    idPerfil: 1,
    idCuenta: 2,
    nombreCompleto: 'Juan Pérez',
    codigoSecuencia: 'ABC123',
    descripcion: 'Perfil de prueba',
    fechaMuestra: '2024-01-15',
    estado: 1,
  },
];

let solicitudes = [
  {
    idSolicitud: 1,
    tipo: 'REGISTRAR',
    datosSolicitud: {
      nombreCompleto: 'Carlos López',
      codigoSecuencia: 'XYZ789',
      fechaMuestra: '2024-02-01',
      descripcion: 'Muestra de sangre',
    },
    estado: 1, // pending
    fechaSolicitud: '2024-03-01',
    idCuentaAdmin: null,
  },
  {
    idSolicitud: 2,
    tipo: 'MODIFICAR',
    datosSolicitud: {
      nombreCompleto: 'Juan Pérez',
      codigoSecuencia: 'ABC123',
      fechaMuestra: '2024-01-15',
      descripcion: 'Actualizar datos',
    },
    estado: 2, // rejected
    fechaSolicitud: '2024-03-02',
    idCuentaAdmin: 1,
  },
];

let logs = [
  {
    idRegistro: 1,
    idTipoAccion: 1,
    idCuenta: 1,
    fecha: '2024-03-01',
    detalle: 'admin@example.com se creo una cuenta',
  },
  {
    idRegistro: 2,
    idTipoAccion: 5,
    idCuenta: 1,
    fecha: '2024-03-02',
    detalle: 'admin@example.com modifico perfil de: Juan Pérez',
  },
];

let cuentaAsignada = null;

// Simulate API delay
const delay = () => new Promise((resolve) => setTimeout(resolve, 500));

const authService = {
  login: async (email, password) => {
    await delay();
    const user = accounts.find((a) => a.email === email);
    if (user) {
      const { password, ...userWithoutPass } = user;
      return { success: true, user: userWithoutPass };
    }
    return { success: false, message: 'Credenciales inválidas' };
  },
  register: async (email, password, nombreCuenta) => {
    await delay();
    const exists = accounts.some((a) => a.email === email);
    if (exists) {
      return { success: false, message: 'El correo ya está registrado' };
    }
    const newId = accounts.length > 0 ? Math.max(...accounts.map((a) => a.idCuenta)) + 1 : 1;
    const newAccount = {
      idCuenta: newId,
      email,
      password,
      nombreCuenta,
      idRol: 1, // default role user
    };
    accounts.push(newAccount);
    const { password: _, ...userWithoutPass } = newAccount;
    return { success: true, user: userWithoutPass };
  },
};

const comptesService = {
  getAll: async () => {
    await delay();
    return [...accounts];
  },
};

const perfilesService = {
  getAll: async () => {
    await delay();
    return [...perfiles];
  },
  getById: async (id) => {
    await delay();
    return perfiles.find((p) => p.idPerfil === id) || null;
  },
  getByEmail: async (email) => {
    await delay();
    // find perfil by account email? For simplicity, return first perfil that matches email via cuentaAsignada? We'll implement later.
    const account = accounts.find((a) => a.email === email);
    if (!account) return null;
    // Find perfil associated with this account (maybe via cuentaAsignada)
    // For mock, return first perfil if any
    return perfiles.length > 0 ? { ...perfiles[0] } : null;
  },
  getMyProfile: async (idCuenta) => {
    await delay();
    return perfiles.find((p) => p.idCuenta === idCuenta) || null;
  },
  updateProfile: async (id, data) => {
    await delay();
    const index = perfiles.findIndex((p) => p.idPerfil === id);
    if (index === -1) throw new Error('Perfil no encontrado');
    perfiles[index] = { ...perfiles[index], ...data, idPerfil: id };
    return perfiles[index];
  },
  deactivateProfile: async (id) => {
    await delay();
    const index = perfiles.findIndex((p) => p.idPerfil === id);
    if (index === -1) throw new Error('Perfil no encontrado');
    perfiles[index].estado = 0;
    return perfiles[index];
  },
  reactivateProfile: async (id) => {
    await delay();
    const index = perfiles.findIndex((p) => p.idPerfil === id);
    if (index === -1) throw new Error('Perfil no encontrado');
    perfiles[index].estado = 1;
    return perfiles[index];
  },
};

const solicitudesService = {
  getAll: async () => {
    await delay();
    return [...solicitudes];
  },
  getPending: async () => {
    await delay();
    return solicitudes.filter((s) => s.estado === 1);
  },
  getRecent: async () => {
    await delay();
    // Return approved or rejected (estado !== 1) sorted by date descending
    return solicitudes
      .filter((s) => s.estado !== 1)
      .sort((a, b) => new Date(b.fechaSolicitud) - new Date(a.fechaSolicitud))
      .slice(0, 10);
  },
  createRequest: async (solicitud) => {
    await delay();
    const newId = solicitudes.length > 0 ? Math.max(...solicitudes.map((s) => s.idSolicitud)) + 1 : 1;
    const request = {
      idSolicitud: newId,
      tipo: solicitud.tipo,
      datosSolicitud: solicitud.datosSolicitud,
      estado: 1, // pending
      fechaSolicitud: new Date().toISOString().split('T')[0],
      idCuentaAdmin: null,
      idCuenta: solicitud.idCuenta,
    };
    solicitudes.push(request);
    return request;
  },
  approveRequest: async (id, adminEmail) => {
    await delay();
    const index = solicitudes.findIndex((s) => s.idSolicitud === id);
    if (index === -1) throw new Error('Solicitud no encontrada');
    solicitudes[index].estado = 1; // approved
    solicitudes[index].idCuentaAdmin = accounts.find((a) => a.email === adminEmail)?.idCuenta || null;
    // If registro or modificar, create/update perfil
    const request = solicitudes[index];
    if (request.tipo === 'REGISTRAR' || request.tipo === 'MODIFICAR') {
      const data = request.datosSolicitud;
      const existingIndex = perfiles.findIndex(
        (p) => p.nombreCompleto === data.nombreCompleto && p.codigoSecuencia === data.codigoSecuencia
      );
      if (existingIndex >= 0) {
        // update
        perfiles[existingIndex] = {
          ...perfiles[existingIndex],
          nombreCompleto: data.nombreCompleto,
          codigoSecuencia: data.codigoSecuencia,
          descripcion: data.descripcion,
          fechaMuestra: data.fechaMuestra,
          estado: 1,
          idCuenta: request.idCuenta,
        };
      } else {
        // create
        const newId = perfiles.length > 0 ? Math.max(...perfiles.map((p) => p.idPerfil)) + 1 : 1;
        perfiles.push({
          idPerfil: newId,
          nombreCompleto: data.nombreCompleto,
          codigoSecuencia: data.codigoSecuencia,
          descripcion: data.descripcion,
          fechaMuestra: data.fechaMuestra,
          estado: 1,
          idCuenta: request.idCuenta,
        });
      }
    } else if (request.tipo === 'BAJA') {
      // deactivate profile based on datosSolicitud? We'll need to find perfil by nombre/completo? For simplicity, ignore.
    } else if (request.tipo === 'RESTAURAR') {
      // activate profile
    }
    // Add log
    await logsService.addLog(adminEmail, request.tipo === 'REGISTRAR' ? 1 : 5, ` procesó solicitud ${request.tipo}`);
    return solicitudes[index];
  },
  rejectRequest: async (id, adminEmail) => {
    await delay();
    const index = solicitudes.findIndex((s) => s.idSolicitud === id);
    if (index === -1) throw new Error('Solicitud no encontrada');
    solicitudes[index].estado = 2; // rejected
    solicitudes[index].idCuentaAdmin = accounts.find((a) => a.email === adminEmail)?.idCuenta || null;
    // Add log
    await logsService.addLog(adminEmail, request.tipo === 'REGISTRAR' ? 1 : 5, ` rechazó solicitud ${request.tipo}`);
    return solicitudes[index];
  },
};

const logsService = {
  getLogs: async () => {
    await delay();
    // Return last 30 logs sorted by id descending
    return logs
      .slice()
      .sort((a, b) => b.idRegistro - a.idRegistro)
      .slice(0, 30);
  },
  addLog: async (email, idTipoAccion, detalle) => {
    await delay();
    const newId = logs.length > 0 ? Math.max(...logs.map((l) => l.idRegistro)) + 1 : 1;
    const account = accounts.find((a) => a.email === email);
    const idCuenta = account ? account.idCuenta : 0;
    const accountEmail = account ? account.email : 'usuario';
    // Get action name from idTipoAccion (we need a mapping)
    const accionMap = {
      1: 'Registro',
      2: 'Modificación',
      3: 'Baja',
      4: 'Restaurar',
      5: 'Modificación de perfil',
    };
    const accionNombre = accionMap[idTipoAccion] || 'Acción';
    const detalleTexto = detalle ? `${detalle}` : '';
    const fullDetalle = `${accountEmail} ${accionNombre.toLowerCase()}${detalleTexto ? ': ' + detalleTexto : ''}`;
    logs.push({
      idRegistro: newId,
      idTipoAccion: idTipoAccion,
      idCuenta,
      fecha: new Date().toISOString().split('T')[0],
      detalle: fullDetalle.trim(),
    });
    return { idRegistro: newId };
  },
};

export {
  authService,
  comptesService,
  perfilesService,
  solicitudesService,
  logsService,
};