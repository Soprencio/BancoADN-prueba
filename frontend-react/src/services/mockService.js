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
    estado: 0, // pendiente (0)
    fechaSolicitud: '2024-03-01',
    idCuentaAdmin: null,
    email: 'juan@example.com', // requester email
    nombreCompleto: 'Carlos López',
    codigoSecuencia: 'XYZ789',
    fechaMuestra: '2024-02-01',
    descripcion: 'Muestra de sangre',
  },
  {
    idSolicitud: 2,
    tipo: 'MODIFICAR',
    estado: 2, // rechazado (2)
    fechaSolicitud: '2024-03-02',
    idCuentaAdmin: 1,
    email: 'juan@example.com', // requester email
    nombreCompleto: 'Juan Pérez',
    codigoSecuencia: 'ABC123',
    fechaMuestra: '2024-01-15',
    descripcion: 'Actualizar datos',
  },
];

let logs = [
  {
    idRegistro: 1,
    fecha: '2024-03-01',
    nombreCuenta: 'Administrador',
    email: 'admin@example.com',
    descripcion: 'admin@example.com se creó una cuenta',
    acciones: 'Registro',
    esAdmin: true,
  },
  {
    idRegistro: 2,
    fecha: '2024-03-02',
    nombreCuenta: 'Administrador',
    email: 'admin@example.com',
    descripcion: 'admin@example.com modificó perfil de Juan Pérez',
    acciones: 'Modificación de perfil',
    esAdmin: true,
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
      // Set the currently assigned cuenta for mock session
      cuentaAsignada = user.idCuenta;
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
    // Optionally set cuentaAsignada on registration? Not needed.
    return { success: true, user: userWithoutPass };
  },
};

const cuentasService = {
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
    const account = accounts.find((a) => a.email === email);
    if (!account) return null;
    const perfil = perfiles.find((p) => p.idCuenta === account.idCuenta);
    return perfil ? { ...perfil } : null;
  },
  getMyProfile: async (email) => {
    await delay();
    const account = accounts.find((a) => a.email === email);
    if (!account) return null;
    return perfiles.find((p) => p.idCuenta === account.idCuenta) || null;
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
  buscarPerfiles: async (termino, tipoBusqueda, rol) => {
    await delay();
    let filtered = perfiles;
    if (rol === 1) {
      filtered = filtered.filter(p => p.estado === 1);
    }
    if (termino.trim() !== '') {
      const lowerTerm = termino.toLowerCase();
      if (tipoBusqueda === 'nombre') {
        filtered = filtered.filter(p =>
          p.nombreCompleto.toLowerCase().includes(lowerTerm)
        );
      } else if (tipoBusqueda === 'codigo') {
        filtered = filtered.filter(p =>
          p.codigoSecuencia.toLowerCase().includes(lowerTerm)
        );
      }
    }
    return filtered.map(p => ({ ...p }));
  },
};

const solicitudesService = {
  getAll: async () => {
    await delay();
    return [...solicitudes];
  },
  getPending: async () => {
    await delay();
    return solicitudes.filter((s) => s.estado === 0);
  },
  getRecent: async () => {
    await delay();
    // Return approved or rejected (estado !== 0) sorted by date descending
    return solicitudes
      .filter((s) => s.estado !== 0)
      .sort((a, b) => new Date(b.fechaSolicitud) - new Date(a.fechaSolicitud))
      .slice(0, 10);
  },
  createRequest: async (solicitud) => {
    await delay();
    const newId = solicitudes.length > 0 ? Math.max(...solicitudes.map((s) => s.idSolicitud)) + 1 : 1;
    // solicitud should contain email of requester email
    const requesterAccount = accounts.find((a) => a.email === solicitud.email);
    const requesterIdCuenta = requesterAccount ? requesterAccount.idCuenta : null;
    const request = {
      idSolicitud: newId,
      tipo: solicitud.tipo,
      estado: 0, // pending
      fechaSolicitud: new Date().toISOString().split('T')[0],
      idCuentaAdmin: null,
      email: solicitud.email, // requester email
      nombreCompleto: solicitud.nombreCompleto,
      codigoSecuencia: solicitud.codigoSecuencia,
      fechaMuestra: solicitud.fechaMuestra,
      descripcion: solicitud.descripcion,
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
      const data = {
        nombreCompleto: request.nombreCompleto,
        codigoSecuencia: request.codigoSecuencia,
        descripcion: request.descripcion,
        fechaMuestra: request.fechaMuestra,
      };
      // Find requester account via email to get idCuenta
      const requesterAccount = accounts.find((a) => a.email === request.email);
      const requesterIdCuenta = requesterAccount ? requesterAccount.idCuenta : null;
      const existingIndex = perfiles.findIndex(p => p.idCuenta === requesterIdCuenta);
      if (existingIndex >= 0) {
        // update
        perfiles[existingIndex] = {
          ...perfiles[existingIndex],
          nombreCompleto: data.nombreCompleto,
          codigoSecuencia: data.codigoSecuencia,
          descripcion: data.descripcion,
          fechaMuestra: data.fechaMuestra,
          estado: 1,
          idCuenta: requesterIdCuenta,
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
          idCuenta: requesterIdCuenta,
        });
      }
    } else if (request.tipo === 'BAJA') {
      // deactivate profile based on requester idCuenta
      const requesterAccount = accounts.find((a) => a.email === request.email);
      const requesterIdCuenta = requesterAccount ? requesterAccount.idCuenta : null;
      const perfilIndex = perfiles.findIndex(p => p.idCuenta === requesterIdCuenta);
      if (perfilIndex !== -1) {
        perfiles[perfilIndex].estado = 0; // inactive
      }
    } else if (request.tipo === 'RESTAURAR') {
      // activate profile based on requester idCuenta
      const requesterAccount = accounts.find((a) => a.email === request.email);
      const requesterIdCuenta = requesterAccount ? requesterAccount.idCuenta : null;
      const perfilIndex = perfiles.findIndex(p => p.idCuenta === requesterIdCuenta);
      if (perfilIndex !== -1) {
        perfiles[perfilIndex].estado = 1; // active
      }
    }
    // Add log
    const accionMap = {
      REGISTRAR: 1,
      MODIFICAR: 2,
      BAJA: 3,
      RESTAURAR: 4,
    };
    const accionId = accionMap[request.tipo] || 5; // default to modificacion de perfil
    await logsService.addLog(adminEmail, accionId, ` procesó solicitud ${request.tipo}`);
    return solicitudes[index];
  },
  rejectRequest: async (id, adminEmail) => {
    await delay();
    const index = solicitudes.findIndex((s) => s.idSolicitud === id);
    if (index === -1) throw new Error('Solicitud no encontrada');
    const request = solicitudes[index];
    solicitudes[index].estado = 2; // rejected
    solicitudes[index].idCuentaAdmin = accounts.find((a) => a.email === adminEmail)?.idCuenta || null;
    // Add log
    const accionMap = {
      REGISTRAR: 1,
      MODIFICAR: 2,
      BAJA: 3,
      RESTAURAR: 4,
    };
    const accionId = accionMap[request.tipo] || 5; // default to modificacion de perfil
    await logsService.addLog(adminEmail, accionId, ` rechazó solicitud ${request.tipo}`);
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
    const newId = logs.length > 0
      ? Math.max(...logs.map((l) => l.idRegistro)) + 1 : 1;
    const account = accounts.find((a) => a.email === email);
    const accountEmail = account ? account.email : 'usuario';
    const accountNombreCuenta = account ? account.nombreCuenta : 'Desconocido';
    const accionMap = {
      1: 'Registro',
      2: 'Modificación',
      3: 'Baja',
      4: 'Restaurar',
      5: 'Modificación de perfil',
    };
    const accionNombre = accionMap[idTipoAccion] || 'Acción';
    logs.push({
      idRegistro: newId,
      fecha: new Date().toISOString().split('T')[0],
      nombreCuenta: accountNombreCuenta,
      email: accountEmail,
      descripcion: detalle ? detalle.trim() : accionNombre.toLowerCase(),
      acciones: accionNombre,
      esAdmin: account ? account.idRol === 2 : false,
    });
    return { idRegistro: newId };
  },
};

export {
  authService,
  cuentasService,
  perfilesService,
  solicitudesService,
  logsService,
};