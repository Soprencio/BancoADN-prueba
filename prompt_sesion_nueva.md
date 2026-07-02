# Contexto del proyecto — SimpleADN

Estás trabajando en el frontend React de una aplicación web de escritorio
llamada **SimpleADN** (Banco de ADN). Antes de hacer cualquier cosa, leé
este contexto completo.

## Arquitectura general

El proyecto tiene tres capas que ya están implementadas y funcionando:

1. **LadoServer** (Java, puerto 4990): servidor de sockets TCP. Maneja
   archivos y base de datos. NO se toca.
2. **BancoADN-Cliente** (Java, puerto 7000): gateway HTTP construido con
   Javalin. Recibe requests HTTP del frontend y los traduce a mensajes de
   socket hacia LadoServer. NO se toca.
3. **frontend-react** (Vite + React, puerto 5173): la SPA que tenés que
   arreglar ahora. Es lo único que vas a modificar en esta sesión.

## Estructura de frontend-react/src/ relevante

```
src/
├── contexts/
│   ├── AuthContext.jsx        ← NO tocar
│   └── ToastContext.jsx       ← NO tocar
├── components/
│   ├── ConfirmationModal.jsx  ← NO tocar
│   └── FormModal.jsx          ← NO tocar
├── services/
│   ├── apiService.js          ← NO tocar
│   └── mockService.js         ← NO tocar
├── pages/
│   ├── Login.jsx              ← NO tocar
│   ├── Register.jsx           ← NO tocar
│   ├── UserDashboard.jsx      ← ROTO, hay que reescribir
│   ├── UserDashboard.css      ← NO tocar (ya existe)
│   ├── AdminDashboard.jsx     ← ROTO, hay que reescribir
│   └── AdminDashboard.css     ← NO tocar (ya existe)
└── App.jsx                    ← tiene un bug menor, corregir solo eso
```

## Lo que YA está funcionando y no debes cambiar

**AuthContext.jsx** provee el hook `useAuth()` que devuelve:
```js
{ user, login, register, logout, loading }
```
Donde `user` tiene esta forma cuando está logueado:
```js
{
  idCuenta: -1,        // siempre -1, el backend no lo devuelve en login
  nombreCuenta: string, // nombre derivado del email
  email: string,
  idRol: 1 | 2         // 1 = Usuario, 2 = Administrador
}
```
La sesión se persiste en `sessionStorage`.

**ToastContext.jsx** provee `useToast()` que devuelve `{ addToast }`.
Uso: `addToast('mensaje', 'success' | 'error' | 'info')`.

**apiService.js** exporta estos servicios con estas firmas exactas:
```js
authService.login(email, password)
authService.register(email, password, nombreCuenta)

perfilesService.getMyProfile(email)        // email, NO idCuenta
perfilesService.buscarPerfiles(termino, tipoBusqueda, rol, userEmail)
perfilesService.updateProfile(id, data, adminEmail)
perfilesService.deactivateProfile(id, adminEmail)
perfilesService.reactivateProfile(id, adminEmail)

solicitudesService.getPending(userEmail)
solicitudesService.getRecent(userEmail)
solicitudesService.createRequest(solicitud)  // objeto plano, ver abajo
solicitudesService.approveRequest(id, adminEmail)
solicitudesService.rejectRequest(id, adminEmail)

logsService.getLogs()
logsService.addLog()   // función vacía, el backend loguea automáticamente
```

Forma del objeto solicitud para `createRequest`:
```js
{
  tipo: 'REGISTRAR' | 'MODIFICAR' | 'BAJA' | 'RESTAURAR',
  email: string,           // email del solicitante
  nombreCompleto: string,  // solo para REGISTRAR y MODIFICAR
  codigoSecuencia: string, // solo para REGISTRAR y MODIFICAR
  descripcion: string,     // solo para REGISTRAR y MODIFICAR
  fechaMuestra: string,    // solo para REGISTRAR y MODIFICAR, formato yyyy-MM-dd
}
```

Forma del objeto perfil que devuelven los servicios:
```js
{
  idPerfil: number,
  nombreCompleto: string,
  codigoSecuencia: string,
  descripcion: string,
  fechaMuestra: string,    // yyyy-MM-dd
  estado: 0 | 1            // 0 = Inactivo, 1 = Activo
}
```

Forma del objeto solicitud que devuelven getPending y getRecent:
```js
{
  idSolicitud: number,
  tipo: string,
  estado: 0 | 1 | 2,      // 0 = Pendiente, 1 = Aprobada, 2 = Rechazada
  email: string,           // email del solicitante
  fechaSolicitud: string,
  nombreCompleto: string,  // puede ser vacío en BAJA y RESTAURAR
  codigoSecuencia: string,
  descripcion: string,
  fechaMuestra: string,
}
```

Forma del objeto log que devuelve getLogs:
```js
{
  idRegistro: number,
  fecha: string,
  nombreCuenta: string,
  email: string,
  descripcion: string,
  acciones: string,
  esAdmin: boolean
}
```

**App.jsx** define las rutas:
- `/login` → LoginPage
- `/register` → RegisterPage
- `/user` → UserDashboard (requiere idRol === 1)
- `/admin` → AdminDashboard (requiere idRol === 2)

**ConfirmationModal** props: `{ isOpen, onClose, title, message, confirmLabel, confirmVariant, onConfirm }`

**FormModal** props: `{ isOpen, onClose, title, fields, submitLabel, onSubmit, initialValues }`
Donde cada field tiene: `{ name, label, type?, required?, initialValue? }`
`onSubmit` recibe un objeto con los valores del formulario indexados por `name`.

## Paleta de colores del proyecto
- Azul primario: `#2b94c8` — acciones positivas, Activo, Aprobada
- Rojo claro: `#e25c6b` — acciones destructivas, Inactivo, Rechazada
- Fondo: `#f3f3f3`
- Texto principal: `#1a1a1a`

---

# Tarea: reescribir UserDashboard.jsx, AdminDashboard.jsx y corregir App.jsx

Ambos dashboards están severamente corruptos (imports duplicados, código
mezclado, strings sin cerrar, funciones cortadas). NO intentes repararlos.
Borrá cada archivo y reescribílo completo desde cero.

## Reglas generales para ambos archivos

- Cada archivo empieza con imports, luego define el componente como función
  nombrada, y termina con `export default NombreComponente`.
- Sin imports duplicados. Sin imports dentro del cuerpo del componente.
- Todos los strings cerrados. Todas las funciones y bloques JSX completos.
- Usá `user.email` (nunca `user.idCuenta`) para llamar a cualquier servicio
  que identifique al usuario — `idCuenta` siempre es -1.
- Todos los llamados a servicios dentro de `try/catch` con `addToast` para
  errores. Los éxitos también muestran toast de confirmación.
- No modifiques ningún archivo fuera de los tres indicados.

---

## USERDASHBOARD.JSX

**Imports** (exactamente estos, sin duplicar):
```js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { solicitudesService, perfilesService } from '../services/apiService';
import { useToast } from '../contexts/ToastContext';
import ConfirmationModal from '../components/ConfirmationModal';
import FormModal from '../components/FormModal';
import './UserDashboard.css';
```

**Estado interno:**
```js
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
```

**Funciones:**

`loadPendingRequests`: llama a `solicitudesService.getPending(user.email)`,
filtra por `s.email === user.email`, guarda en `pendingRequests`. En catch:
addToast de error.

`hasPending(tipo)`: `return pendingRequests.some(p => p.tipo === tipo)`

`handleVerPerfil`: `setLoadingProfile(true)`, llama a
`perfilesService.getMyProfile(user.email)`, guarda en `perfil`,
`setShowPerfil(true)`, `setLoadingProfile(false)`. En catch: addToast.

`handleOpenRegistrar`: `setRequestType('REGISTRAR')`,
`setFormInitialValues({})`, `setShowRequestModal(true)`.

`handleOpenModificar`: llama a `getMyProfile(user.email)`, si hay perfil
precarga `formInitialValues` con sus campos, `setRequestType('MODIFICAR')`,
`setShowRequestModal(true)`. En catch: addToast.

`handleRequestSubmit(values)`: construye objeto solicitud con tipo, email y
los campos del form, llama a `createRequest(solicitud)`. En éxito: addToast
"Solicitud enviada", cerrar modal, `loadPendingRequests()`. En catch: addToast.

`handleConfirmBaja`: llama a `createRequest({ tipo:'BAJA', email:user.email })`.
En éxito: addToast "Solicitud de baja enviada", cerrar modal,
`loadPendingRequests()`. En catch: addToast.

`handleConfirmRestaurar`: igual con tipo `'RESTAURAR'`.

`handleBuscar`: llama a `perfilesService.buscarPerfiles(searchTerm,
searchType, user.idRol, user.email)`, guarda en `searchResults`.
En catch: addToast.

`handleLogout`: `logout()`, `navigate('/login')`.

Al montar: `useEffect(() => { if (user) loadPendingRequests(); }, [user])`

**Layout JSX:**

```
<div className="user-dashboard">
  <header className="topbar">
    <div className="topbar-left">
      [ícono usuario SVG o emoji] user.nombreCuenta — user.email
    </div>
    <div className="topbar-center">
      <h1><span style rojo>Simple</span><span style azul>ADN</span></h1>
    </div>
    <div className="topbar-right">
      <button onClick={() => setShowSearch(true)}>🔍 Buscar Perfil</button>
      <button onClick={handleLogout}>Cerrar Sesión</button>
    </div>
  </header>

  <main className="main-content">
    {showSearch ? (
      VISTA BÚSQUEDA — ver abajo
    ) : (
      <section className="dashboard-body">
        PANEL IZQUIERDO + PANEL DERECHO — ver abajo
      </section>
    )}
  </main>

  [Modales al final del return, fuera del main]
</div>
```

**VISTA BÚSQUEDA** (cuando `showSearch === true`):
```
<div className="search-view">
  <button onClick={() => setShowSearch(false)}>← Volver</button>
  <h2>Buscar Perfiles</h2>
  <div className="search-bar">
    <select value={searchType} onChange={e => setSearchType(e.target.value)}>
      <option value="nombre">Por Nombre</option>
      <option value="codigo">Por Código</option>
    </select>
    <input value={searchTerm} onChange={e => setSearchTerm(e.target.value)}
      placeholder="Buscar..." />
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
```

**PANEL IZQUIERDO** (cuando `showSearch === false`):
```
<div className="left-panel">
  <h3>Mis Solicitudes</h3>
  <div className="btn-group">
    [Botón Solicitar Registro] → handleOpenRegistrar
      {hasPending('REGISTRAR') && <span className="badge">Pendiente</span>}

    [Botón Solicitar Modificación] → handleOpenModificar
      {hasPending('MODIFICAR') && <span className="badge">Pendiente</span>}

    [Botón Solicitar Baja, estilo danger] → setShowConfirmBaja(true)
      {hasPending('BAJA') && <span className="badge">Pendiente</span>}

    [Botón Solicitar Restauración] → setShowConfirmRestaurar(true)
      {hasPending('RESTAURAR') && <span className="badge">Pendiente</span>}
  </div>
</div>
```

**PANEL DERECHO** (cuando `showSearch === false`):
```
<div className="right-panel">
  <h3>Mi Perfil Genético</h3>
  <button onClick={handleVerPerfil}>Consultar mis datos</button>

  {showPerfil && (
    loadingProfile ? <p>Cargando...</p>
    : perfil === null ? (
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
```

**Modales:**
```jsx
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
```

---

## ADMINDASHBOARD.JSX

**Imports** (exactamente estos, sin duplicar):
```js
import React, { useState, useEffect } from 'react';
import { useAuth } from '../contexts/AuthContext';
import { useNavigate } from 'react-router-dom';
import { solicitudesService, perfilesService, logsService } from '../services/apiService';
import { useToast } from '../contexts/ToastContext';
import ConfirmationModal from '../components/ConfirmationModal';
import FormModal from '../components/FormModal';
import './AdminDashboard.css';
```

**Estado interno:**
```js
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
```

**Funciones de carga:**

`cargarPendientes`: llama a `solicitudesService.getPending(user.email)`,
guarda en `solicitudesPendientes`. En catch: addToast.

`cargarPerfiles(term, type)`: llama a `perfilesService.buscarPerfiles(
term || '', type || 'Todos', user.idRol, user.email)`,
guarda en `perfilesList`. En catch: addToast.

`cargarLogs`: llama a `logsService.getLogs()`, guarda en `logsList`.
En catch: addToast.

`cargarUltimas`: llama a `solicitudesService.getRecent(user.email)`,
guarda en `ultimasList`. En catch: addToast.

`useEffect` al montar: `cargarPendientes()`.

`useEffect` al cambiar `vistaActiva`:
- `'pendientes'` → `cargarPendientes()`
- `'perfiles'` → `cargarPerfiles('', 'Todos')`
- `'logs'` → `cargarLogs()`
- `'ultimas'` → `cargarUltimas()`

**Funciones de acción:**

`handleAprobar(id)`: abre `confirmModal` con mensaje "¿Aprobás la
solicitud #id?". `onConfirm` llama a `approveRequest(id, user.email)`,
cierra modal, addToast "Aprobada", `cargarPendientes()`. En catch: addToast.

`handleRechazar(id)`: igual con `rejectRequest`, mensaje "¿Rechazás?",
toast "Rechazada".

`handleDarDeBaja(perfil)`: abre confirmModal. onConfirm llama a
`deactivateProfile(perfil.idPerfil, user.email)`, addToast, `cargarPerfiles`.

`handleRestaurar(perfil)`: igual con `reactivateProfile`.

`handleEditarSubmit(values)`: llama a `updateProfile(editModal.perfil.idPerfil,
values, user.email)`, cierra editModal, addToast, `cargarPerfiles`.

`handleLogout`: `logout()`, `navigate('/login')`.

**Layout JSX:**

```
<div className="admin-dashboard">
  <header className="topbar">
    <div className="topbar-left">
      {user.email}
    </div>
    <div className="topbar-center">
      <h1><span style rojo>Simple</span><span style azul>ADN</span></h1>
    </div>
    <div className="topbar-right">
      [Botón "Solicitudes Pendientes" → setVistaActiva('pendientes'), activo si vistaActiva==='pendientes']
      [Botón "Administrar Perfiles" → setVistaActiva('perfiles')]
      [Botón "Logs" → setVistaActiva('logs')]
      [Botón "Últimas Solicitudes" → setVistaActiva('ultimas')]
      [Botón "Cerrar Sesión" → handleLogout]
    </div>
  </header>

  <main className="main-content">
    {vistaActiva === 'pendientes' && VISTA_PENDIENTES}
    {vistaActiva === 'perfiles' && VISTA_PERFILES}
    {vistaActiva === 'logs' && VISTA_LOGS}
    {vistaActiva === 'ultimas' && VISTA_ULTIMAS}
  </main>

  [Modales]
</div>
```

**VISTA_PENDIENTES:**
```
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
          <button className="btn btn-success"
            onClick={() => handleAprobar(sol.idSolicitud)}>Aprobar</button>
          <button className="btn btn-danger"
            onClick={() => handleRechazar(sol.idSolicitud)}>Rechazar</button>
        </div>
      </div>
    ))
  )}
</div>
```

**VISTA_PERFILES:**
```
<div className="vista-perfiles">
  <h2>Administrar Perfiles</h2>
  <div className="search-bar">
    <select value={searchType} onChange={e => setSearchType(e.target.value)}>
      <option value="Todos">Todos</option>
      <option value="nombre">Por Nombre</option>
      <option value="codigo">Por Código</option>
    </select>
    <input value={searchTerm} onChange={e => setSearchTerm(e.target.value)}
      placeholder="Buscar..." />
    <button onClick={() => cargarPerfiles(searchTerm, searchType)}>Buscar</button>
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
        <button onClick={() => setEditModal({ open: true, perfil: p })}>Modificar</button>
        {p.estado === 1 ? (
          <button className="btn btn-danger"
            onClick={() => handleDarDeBaja(p)}>Dar de baja</button>
        ) : (
          <button className="btn btn-success"
            onClick={() => handleRestaurar(p)}>Restaurar</button>
        )}
      </div>
    </div>
  ))}
</div>
```

**VISTA_LOGS:**
```
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
```

**VISTA_ULTIMAS:**
```
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
```

**Modales:**
```jsx
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
  submitLabel="Guardar cambios"
  onSubmit={handleEditarSubmit}
/>
```

---

## APP.JSX — corrección puntual

En App.jsx hay un nombre inconsistente: el componente de protección por rol
se define como `PrivateRoute` en un lugar y se usa como `<PrivateRole>` en
el JSX. Unificá a `PrivateRole` en toda la definición. Eliminá cualquier
definición de `PrivateRoute` que no se use. No toques nada más de App.jsx.

---

## Al terminar

1. Corré `npm run dev` desde la carpeta `frontend-react/`.
2. Si hay errores de compilación, corregílos antes de reportar que terminaste.
3. Mostrame las primeras 15 líneas de `UserDashboard.jsx` y
   `AdminDashboard.jsx` para confirmar que los imports están limpios
   y el componente está correctamente definido con `export default` al final.
