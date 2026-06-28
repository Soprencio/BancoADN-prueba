# Frontend React Architecture (SimpleADN)

This document outlines the proposed frontend architecture for migrating the Java Swing client (BancoADN‑Cliente) to a React SPA.  
It maps existing Swing screens/pantallas to React pages/components, identifies the services that will encapsulate the backend‑controller logic, lists all user‑initiated actions/events, and notes any open questions that need clarification before implementation.

---

## 1. Mapping of Swing Screens → React Pages / Components

| Swing Class (Pant / Menu) | Purpose (Swing) | Proposed React Entity | Notes |
|---------------------------|-----------------|-----------------------|-------|
| `BancoADN_Grupo6_Pant_IniciarSesion` | Login screen (email + password) + link to register | **LoginPage** (can also contain the register toggle) | Handles email/password validation, calls `authService.login`. Shows error toast/modal on failure. |
| `BancoADN_Grupo6_Pant_CrearCuenta` | Account creation (nombre, email, password) | **RegisterPage** (or merged into LoginPage as a tab) | Validates fields, calls `authService.register`. On success redirects to login or directly to appropriate dashboard. |
| `BancoADN_Grupo6_MenuUsuario` (not a Pant) | Usuario dashboard after login | **UserDashboard** (main layout) | Contains:<br>• TopBar (user avatar + email, **Buscar Perfil** button, logout)<br>• Four action buttons (Solicitar Registro, Modificación, Baja, Restaurar)<br>• “Ver mi Perfil” panel<br>• Tab/view for **BuscarPerfil** |
| `BancoADN_Grupo6_Pant_BuscarPerfil` | Profile search (by name or código) visible to Usuario | **ProfileSearch** (tab/component inside UserDashboard) | Shows search bar (toggle Nombre/Código), list of **active** profile cards (nombre, código, fecha, descripción, estado chip). |
| `BancoADN_Grupo6_Pant_SolicitarPerfil` | Form to request a new profile | **RequestProfileModal** (re‑usable Form Modal) | Used when user clicks “Solicitar Registro de Perfil”. Calls `solicitudesService.createRequest({type: 'REGISTRAR', datos: {...}})`. |
| `BancoADN_Grupo6_Pant_SolicitarModPerfil` | Form to request modification of own profile | **RequestModificationModal** (same Form Modal, pre‑filled) | Used for “Solicitar Modificación de Perfil”. Sends request type `MODIFICAR`. |
| `BancoADN_Grupo6_Pant_ModificarPerfilAdmin` | Admin direct edit of a profile (bypassing request flow) | **EditProfileModal** (Form Modal, admin‑only) | Triggered from the **Administrar Perfiles** view when admin clicks “Modificar” on a profile card. Calls `perfilesService.updateProfile(id, data)`. |
| `BancoADN_Grupo6_Pant_AdminPerfiles` | Admin view: list all profiles (active & inactive) with actions | **ManageProfilesPage** (tab inside AdminDashboard) | Shows search bar (Nombre/Código), list of all profile cards. Each card shows:<br>• Nombre, código, fecha, descripción, estado chip (Activo/Inactivo)<br>• Action button: **Dar de baja** (if Activo) / **Restaurar** (if Inactivo) → Confirmation Modal<br>• **Modificar** button → EditProfileModal |
| `BancoADN_Grupo6_Pant_Logs` | Admin view: last 30 log entries (newest first) | **LogsPage** (tab inside AdminDashboard) | Shows refresh button, list of log rows (idLog, responsable with “Admin” tag if applicable, fecha, acción). Calls `logsService.fetchLogs()` on mount and on refresh. |
| `BancoADN_Grupo6_Pant_UltimasSolicitudes` | Admin view: last 10 resolved requests (approved/rejected) | **RecentRequestsPage** (tab inside AdminDashboard) | Shows list of request items (tipo, usuario email, admin resolver, status chip Aprobada/Rechazada). Calls `solicitudesService.fetchRecentRequests()`. |
| `BancoADN_Grupo6_MenuAdmin` (not a Pant) | Admin dashboard after login | **AdminDashboard** (main layout) | Contains:<br>• TopBar (greeting “Hola, Admin”, logo, logout)<br>• Three tab buttons: **Administrar Perfiles**, **Logs**, **Últimas Solicitudes** (each swaps main content area)<br>• Respective page components rendered in the main area. |
| (Implicit) “Ver mi Perfil” panel inside UserDashboard | Show logged‑in user’s profile data | **ProfilePanel** (component inside UserDashboard) | Displays nombre, código de secuencia, fecha de muestra, descripción, estado chip. Data can be taken from auth state or fetched via `perfilesService.getMyProfile()` if needed. |

### Re‑usable Modal Components
| Modal | Usage |
|-------|-------|
| **ConfirmationModal** | Used for: Baja, Restaurar, Rechazar solicitud, Logout (optional), any destructive action. Props: title, message, confirmText, cancelText, onConfirm, onCancel. |
| **FormModal** | Used for: Register (if separate), Request Profile, Request Modification, Admin Edit Profile. Props: title, fields (dynamic), submitLabel, onSubmit, onCancel. |
| **ErrorToast / ErrorModal** | For validation errors, failed requests, login errors. Can be a toast (top‑right) or a modal depending on severity. |

---

## 2. Service Layer (React side)

Each service will encapsulate the calls that the corresponding `Ctrl_*` classes currently perform via the socket client.  
Initially the services will be **mocked** (in‑memory data structures) to allow UI development.  
In a later phase they will be switched to `fetch`/`axios` calls that talk to a thin HTTP API wrapped around the existing Java controller methods (still using the same socket‑based server‑side logic).

| Service | Responsibilities (mirrors Ctrl) |
|---------|
| **authService** | `login(email, password) → {token?, user?}`<br>`register(nombre, email, password) → success/error`<br>`logout()` |
| **solicitudesService** | `createRequest(type, datos) → requestId`<br>`getPendingRequests() → [{id, tipo, usuarioEmail, fecha, preview}]`<br>`getRecentRequests(limit = 10) → [{id, tipo, usuarioEmail, adminEmail, fecha, estado}]`<br>`approveRequest(id) → success`<br>`rejectRequest(id) → success` |
| **perfilesService** | `getMyProfile() → perfil`<br>`getProfileByEmail(email) → perfil`<br>`getAllProfiles() →[{id, nombre, codigo, fecha, descripcion, estado, email}]`<br>`updateProfile(id, datos) → success`<br>`deactivateProfile(id) → success` (baja)<br>`reactivateProfile(id) → success` (restaurar) |
| **logsService** | `getLogs(limit = 30) →[{idLog, responsableNombre, responsableEmail, esAdmin, fecha, accion}]` |

**Implementation notes**

* All service methods return a `Promise` that resolves to the data or rejects with an error object.
* In the mock layer, we can store data in simple JavaScript arrays/objects and simulate latency with `setTimeout`.
* When the HTTP API is added, each service method will map to an endpoint (e.g., `POST /api/auth/login`, `POST /api/solicitudes`, `GET /api/perfiles`, etc.).
* Error handling: services throw or return rejected promises; UI components translate these to toasts/modals.

---

## 3. Events / Actions (UI → Service)

Below is a concise list of every user‑initiated interaction identified from the Swing controllers and the spec.  
Each entry shows the UI element, the triggered action, and the service method that will eventually be called.

### 3.1 Login / Register
| UI Element | Action | Service Call |
|------------|--------|--------------|
| Login button (`btnIniciarSesion`) | Validate fields → login request | `authService.login(email, password)` |
| Register link (`lblRegistro`) | Switch to register view | — |
| Register button (`btnIngresar`) | Validate fields → register request | `authService.register(nombre, email, password)` |
| Back link (`lblVolver`) | Return to login | — |
| Logout button (any TopBar) | End session | `authService.logout()` (clear auth state) |

### 3.2 Usuario Dashboard
| UI Element | Action | Service Call |
|------------|--------|--------------|
| “Buscar Perfil” TopBar button | Switch to profile search tab/view | — |
| “Solicitar Registro de Perfil” button | Open **RequestProfileModal** (empty) | — |
| “Solicitar Modificación de Perfil” button | Open **RequestModificationModal** pre‑filled with current profile data | — |
| “Solicitar Baja de Perfil” button | Open **ConfirmationModal** (are you sure?) → on confirm: `solicitudesService.createRequest({type: 'BAJA', datos: null})` |
| “Solicitar Restauración de Perfil” button | Open **ConfirmationModal** → on confirm: `solicitudesService.createRequest({type: 'RESTAURAR', datos: null})` |
| “Ver mi Perfil” button | Show/hide **ProfilePanel**; fetch data if not already in auth state | `perfilesService.getMyProfile()` |
| ProfileSearch search input (with debounce) | Change query (nombre/código) → filter list | (client‑side filter on cached list from `perfilesService.getAllProfiles()` or dedicated search endpoint) |
| Profile card click (optional) | Show profile details in a modal or side panel | `perfilesService.getProfileByEmail(email)` (if needed) |

### 3.3 Admin Dashboard (Tab Switching)
| UI Element | Action | Service Call |
|------------|--------|--------------|
| TopBar “Administrar Perfiles” button | Show **ManageProfilesPage** | — |
| TopBar “Logs” button | Show **LogsPage** | — |
| TopBar “Últimas Solicitudes” button | Show **RecentRequestsPage** | — |
| Logout button (TopBar) | End session | `authService.logout()` |

### 3.4 Manage Profiles Page (Admin)
| UI Element | Action | Service Call |
|------------|--------|--------------|
| Search bar (toggle + input) | Filter displayed profiles | (client‑side filter on list from `perfilesService.getAllProfiles()`) |
| Profile card “Dar de baja” button (Activo) | Open **ConfirmationModal** → on confirm: `perfileservice.deactivateProfile(id)` |
| Profile card “Restaurar” button (Inactivo) | Open **ConfirmationModal** → on confirm: `perfileservice.reactivateProfile(id)` |
| Profile card “Modificar” button | Open **EditProfileModal** pre‑filled with profile data → on submit: `perfileservice.updateProfile(id, editedData)` |

### 3.5 Logs Page (Admin)
| UI Element | Action | Service Call |
|------------|--------|--------------|
| Refresh button (“Actualizar”) | Reload list | `logsService.getLogs(limit=30)` (called on mount and on click) |

### 3.6 Recent Requests Page (Admin)
| UI Element | Action | Service Call |
|------------|--------|--------------|
| (Auto‑load on mount) | Fetch list | `solicitudesService.getRecentRequests(limit=10)` |
| (Optional) Click on a row | Show detailed request modal (if needed) | `solicitudesService.getRequestById(id)` |

### 3.7 Request Modals (User)
| UI Element | Action | Service Call |
|------------|--------|--------------|
| Form Submit (`Enviar solicitud` / `Guardar`) | Validate fields → send request | `solicitudesService.createRequest({type: 'REGISTRAR' | 'MODIFICAR', datos: {nombre, codigo, fecha, descripcion}})` |
| Cancel / X | Close modal | — |

### 3.8 Confirmation Modal (General)
| UI Element | Action | Service Call |
|------------|--------|--------------|
| Confirm button | Perform the underlying action (see rows above) | *see respective service calls* |
| Cancel button | Close modal | — |

### 3.9 Error Handling (global)
| Situation | UI Response |
|-----------|-------------|
| Service request fails (network error, validation error from backend) | Show error toast (or modal for critical errors). |
| Validation fails locally (empty fields, invalid email/date) | Show inline error or toast, block submission. |

---

## 4. Open Questions / Ambiguities

1. **User profile retrieval**  
   - In `MenuUsuario` the method `mostrarPerfil` is called from the controller after a `BuscarIDNOM` request (search by ID/name). The spec’s “Ver mi Perfil” panel likely should show the logged‑in user’s own profile without an extra search. Should we fetch it via `perfileservice.getMyProfile()` (which would require an endpoint that returns the profile tied to the auth token) or reuse the search endpoint with the user’s email/ID? Clarify the intended backend call.

2. **Search behavior for “Buscar Perfil” (Usuario) vs “Administrar Perfiles” (Admin)**  
   - The spec says usuario sees only **active** profiles; admin sees **all**. Should the backend expose a flag (`activo=1`) or should the UI filter locally after fetching all profiles? Clarify whether the server already filters by `estado` when the request comes from a user vs admin.

3. **Exact format of log entries**  
   - The `Logs.txt` file contains lines like `idLog - idTipoAccion - idCuenta - fecha - detalle`. The server’s `ObtenerLogsAdmin` transforms them into `mail - idCuenta - nombreCuenta - email - detalle - fecha - nombreAccion - adminFlag`. The UI must display: `idLog`, responsible (name + optional “Admin” tag), fecha, acción. Need confirmation that the “detalle” field corresponds to the description shown in the UI (the free‑text part of the log).  

4. **Request detail view (optional)**  
   - The spec does not mandate a detailed view when clicking a request in “Últimas Solicitudes” or “Solicitudes Pendientes”. If desired, we would need an endpoint to fetch a single request by ID (`solicitudesService.getRequestById`). Confirm whether this is required.

5. **Date format for profile fields**  
   - The UI expects dates in `yyyy‑MM-dd` (as per validation in `Ctrl_SolicitarPerfil`). Confirm that the backend stores and returns dates in this exact format.

6. **Role‑based UI visibility**  
   - The `idRol` field in `CuentaPersonal` determines whether the user sees the Usuario or Admin dashboard after login. Ensure the auth service returns the role and the router/guard redirects accordingly.

7. **Pending requests visual indicator**  
   - The spec mentions a small status chip (“Pendiente” in light‑blue) on the four request buttons in the Usuario dashboard when a request of that type is already pending. This implies the UI needs to query pending requests for the current user (maybe via `solicitudesService.getMyPendingRequests()`). Verify if such an endpoint exists or if we should filter the global pending list by the user’s email.

8. **Toast vs Modal for errors**  
   - The spec mentions toast notifications for success/error and also error modals for validation failures. Decide on a consistent pattern (e.g., use toast for non‑blocking feedback, modal only for blocking confirmations). Clarify any specific cases where a modal is mandatory.

9. **Styling assets**  
   - The design reference includes `Topbar.png` (admin) and `TopbarUser.png` (user) plus a DNA helix watermark. Need confirmation on the exact assets to use and whether they should be imported as static images or SVGs.

10. **Routing structure**  
    - Should we use a wrapper `<Router>` with protected routes (`/login`, `/register`, `/dashboard/usuario/*`, `/dashboard/admin/*`) or keep a single‑page layout with conditional rendering based on auth state? Provide recommendation.

---

## 5. Next Steps

1. **Create the React project skeleton** (if not already set up) using Vite (`npm create vite@latest frontend-react -- --template react`).  
2. Implement the **mock service layer** with in‑memory stores mirroring the Java domain objects.  
3. Build the **layout components** (TopBar, Sidebar‑less navigation, modals, toast container).  
4. Develop each page/component according to the mapping above, hooking them to the mock services.  
5. Once UI is stable, replace mock services with real `fetch` calls to the upcoming HTTP API (to be added inside `client-java` as a thin wrapper over the existing controller methods).  
6. Conduct UI review against the provided design reference (Stitch) and the SPEC to ensure visual and behavioral fidelity.  

---

*Prepared by the frontend architect – 2025‑08‑27*