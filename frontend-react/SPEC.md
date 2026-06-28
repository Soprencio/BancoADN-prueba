# Role & Context
You are an expert UI/UX designer and frontend developer. Your task is to generate a comprehensive, highly interactive, and beautiful **desktop web interface** (designed to be viewed on a PC, wide-screen layout — not mobile-first) for a software application called "SimpleADN", a DNA Profile Bank (Banco de ADN) with a client-server architecture.

## Data Model (for context — drives what each screen must show)
- **CuentaPersonal**: idCuenta, nombreCuenta, email, contraseña, idRol (1 = Usuario, 2 = Administrador).
- **Rol**: idRol, nombreRol.
- **PerfilGenetico**: idPerfil, nombreCompleto, codigoSecuencia, descripcion, estado (1 = Activo, 0 = Inactivo), fechaMuestra, idCuenta. → These are exactly the fields every "profile card" or "profile detail" view must display.
- **Solicitud**: idSolicitud, tipo (REGISTRAR, MODIFICAR, BAJA, RESTAURAR), estado (0 = Pendiente, 1 = Aprobada, 2 = Rechazada), datosSolicitud, idPerfil (nullable), fechaCreacion, sender account.
- **CuentaAsignada**: links a resolved Solicitud to the Administrador account that resolved it + fechaResolución. → This is how "Últimas Solicitudes" knows which admin approved/rejected each one.
- **Registro / Log** (internally "Registro", shown to the user as "Logs"): idRegistro, responsable (account), idTipoAccion → nombreAccion, fechaRegistro, detalles. If the responsable is an Administrador, the log row gets a small **"Admin"** tag next to their name; if it's a regular Usuario action (e.g. sending a request), no tag is shown.

## Roles & Flow
Login branches the app into two completely separate experiences based on `idRol`: a **Usuario dashboard** and an **Administrador dashboard**. A Usuario never edits data directly — every write action (register/modify/deactivate/restore a profile) creates a Solicitud that an Administrador must approve or reject before it impacts the database.

---

# Design System & Visual Aesthetics

- **Platform:** Desktop web app only. Design for a wide viewport (≥1280px), generous horizontal use of space (multi-column topbars, side-by-side cards), no hamburger menus — all primary navigation lives in the TopBar as visible buttons.

- **Color Palette — REVISED:** Move away from the deep plum/burgundy tones used previously (`#410c2f`, `#851a36` felt too dark/heavy). Instead, build the palette around **lighter, friendlier blues and reds**:
  - **Primary Blue** (keep): `#2b94c8` — primary actions, active states, headers, search bar, "Aprobar/Activo/Aceptar" semantics.
  - **Light Blue tint**: a soft, airy tint of the primary blue (e.g. around `#eaf5fb`–`#d6ecf6`) for card backgrounds, hover states, and the canvas behind content sections.
  - **Light/Soft Red** (replace the burgundy): a brighter, lighter red/coral (e.g. around `#e25c6b`–`#f08a96`, NOT a dark wine tone) — used for "Rechazar/Inactivo/Rechazada/Dar de baja" semantics, destructive buttons, and error states. Keep it light enough to feel modern, not alarming or heavy.
  - **Off-White/Light Gray** `#f3f3f3` — primary app canvas background.
  - Use the blue/red pair consistently as a binary status language across the whole app: blue = positive/active/approve, red(light) = negative/inactive/reject. Avoid mixing in the old dark plum/wine anywhere.

- **Background motif:** Every screen (login, user dashboard, admin dashboard, and every modal) must keep a subtle **DNA double-helix watermark/logo** in the background — low opacity (5–10%), placed so it never competes with text or data. Treat it as the app's visual signature, present at all times.

- **Micro-interactions (desktop-tuned):**
  - Smooth `cubic-bezier(0.4, 0, 0.2, 1)` transitions between views/tabs.
  - Hover states on every clickable element (cards lift slightly with a soft shadow, buttons darken/brighten a touch).
  - Kinetic, fade-masked scrolling on every list (requests, logs, profile search results).
  - Floating labels and focus-glow on text inputs.
  - Skeleton loaders while lists/forms fetch data.
  - Toast notifications (top-right corner) for every success/error confirmation described below.

---

# Popups & Modals (must be designed as first-class, reusable components)
Three distinct modal types are needed throughout the app — design clear, reusable templates for each:

1. **Confirmation Modal** — centered, compact, used for irreversible/important actions (Dar de baja, Restaurar, Rechazar solicitud, Logout). Icon + short question ("¿Estás seguro que querés dar de baja este perfil?") + two buttons (Cancelar / Confirmar, confirm button colored blue or light-red depending on whether the action is positive or destructive).
2. **Form Modal** — larger centered panel with input fields, used for: Registrar Perfil, Modificar Perfil (user request version), Modificar Perfil (admin direct-edit version). Floating-label inputs, inline validation, "Cancelar" / "Enviar" (or "Guardar") buttons.
3. **Error Modal / Toast** — used for validation errors, failed submissions, login failures. Light-red accent, short message, single "Entendido" dismiss button (for modal version) or auto-dismiss toast (for lighter inline errors).

---

# Screen Workflow & Functional Requirements

## 1. Login / Crear Cuenta Screen
- Single screen with a toggle or tab between **"Iniciar sesión"** and **"Crear cuenta"** (no separate full pages needed — keep it as one elegant card centered on the DNA-helix-watermarked canvas).
- Login fields: Email, Contraseña. Crear cuenta fields: Nombre completo, Email, Contraseña.
- Inline format validation (light-red border + helper text) and an Error Modal/banner for "Email o contraseña incorrectos" on failed login.
- On success, route to the Usuario or Administrador dashboard based on role — no visible difference in this screen, the branching is invisible to the user at this point.

## 2. Usuario — Main Dashboard

### 2.1 TopBar (Usuario variant)
- **Center:** app title/logo **"SimpleADN"**.
- **Left side**, in this order: (a) a user-avatar icon with the account's **nombre** and **email** displayed next to it; (b) a **"Buscar Perfil"** button with a magnifying-glass icon.
- **Right side:** a small logout icon/button.
- Clicking **"Buscar Perfil"** opens a new tab/view (see 2.4 below) — it does not replace the whole dashboard, think of it as a switchable tab alongside the main dashboard content.

### 2.2 Action Buttons (4 request types)
Prominently displayed as four clear buttons/cards in the main dashboard body:
- **Solicitar Registro de Perfil** → opens a **Form Modal** with fields Nombre completo, Código de secuencia (ADN), Fecha de muestra, Descripción, and a "Enviar solicitud" button.
- **Solicitar Modificación de Perfil** → opens the same kind of **Form Modal**, pre-filled with the user's current profile data, editable.
- **Solicitar Baja de Perfil** → opens a **Confirmation Modal** only (no form) warning that the profile will be hidden until restored.
- **Solicitar Restauración de Perfil** → opens a **Confirmation Modal** only (no form).
- Each button shows a small status chip if the user already has a pending request of that type (e.g. "Pendiente" in light-blue), and submitting any of them shows a success toast confirming the request was sent.

### 2.3 "Ver mi Perfil" Panel
- A dedicated quadrant/panel in the dashboard with a button at the top, e.g. **"Ver mi Perfil"**.
- Below the button is an **empty content area** by default. On click, the system fetches and dumps the user's profile text into this area: Nombre completo, Código de secuencia, Fecha de muestra, Descripción, and an Activo/Inactivo status chip.
- If the user has no profile, or it's inactive, show the appropriate inline message inside this same panel (no profile → CTA to "Solicitar Registro"; inactive → CTA to "Solicitar Restauración") instead of the data dump.

### 2.4 Buscar Perfil Tab/View
- Opened from the TopBar button. Contains a search bar (with a Nombre/Código toggle) and, below it, a **scrollable list of cards**, each card representing another account's DNA profile, showing the same fields as the Perfil panel (Nombre completo, Código de secuencia, Fecha de muestra, Descripción, Activo status chip).
- Only active profiles are shown to a Usuario. Empty-state and lazy-loading skeleton states apply here as well.

## 3. Administrador — Main Dashboard

### 3.1 TopBar (Administrador variant)
- **Center:** app title/logo **"SimpleADN"**.
- **Left side:** the admin's email.
- **Right side:** three buttons — **"Administrar Perfiles"**, **"Logs"**, **"Últimas Solicitudes"** — each switches the main content area to its respective view (tab-like behavior). A small logout icon sits at the far right.

### 3.2 Default Landing View — Solicitudes Pendientes
- The screen the Administrador sees immediately after login.
- A **scrollable list of cards**, each representing a pending Solicitud, showing: sender's **nombre** and **email**, the **tipo** of request (Registrar/Modificar/Baja/Restaurar — tag-styled in light blue), and a short **descripción**/preview of the submitted data.
- Each card has two buttons: **"Aceptar"** (light blue) and **"Rechazar"** (light red).
  - Aceptar → row collapses with a smooth animation, the system auto-applies the underlying action to the profile, success toast.
  - Rechazar → opens a **Confirmation Modal**, then the card collapses, marked Rechazada, no profile change, toast confirmation.
- Empty state when there are no pending requests ("No hay solicitudes pendientes" + calm illustration with the DNA motif).

### 3.3 Administrar Perfiles View
- Scrollable list of profile cards (same visual language as the Usuario's "Buscar Perfil" cards: Nombre completo, Código de secuencia, Fecha de muestra, Descripción) but here **every profile is shown, active or inactive**, and each card additionally includes:
  - A clear **Activo/Inactivo** status message/chip (blue vs light-red).
  - A status-dependent action button: **"Dar de baja"** (light red) if the profile is Activo, or **"Restaurar"** (blue) if it's Inactivo — opens the corresponding **Confirmation Modal**.
  - A third button, **"Modificar"**, always available, which opens a **Form Modal** pre-filled with the profile's current data, letting the admin manually edit and save changes directly (bypassing the request flow).
- Includes the same search bar (Nombre/Código toggle) as the Usuario's search, for filtering this list.

### 3.4 Logs View
- A scrollable, chronological list showing the **last 30 logs** (business-rule limit), ordered newest → oldest.
- Each log row shows: **idLog** (small/secondary), **responsable** (account name — with a visible **"Admin"** tag next to it if the responsable is an Administrador, no tag if it's a regular Usuario), **fecha**, and the **acción realizada** (e.g. "Inició sesión", "Modificó perfil", "Aprobó solicitud de baja", "Envió solicitud de registro").
- An explicit **"Actualizar"** refresh button/icon at the top of the list.

### 3.5 Últimas Solicitudes View
- A scrollable list showing the **last 10 resolved solicitudes** (Aprobadas or Rechazadas only — never Pendientes), ordered newest → oldest.
- Each item shows: **tipo de solicitud**, the **usuario** who sent it, the **administrador** who resolved it, and a clear **Aprobada/Rechazada** status chip (blue/light-red).

---

# Deliverable Format
Provide a complete, production-ready, interactive desktop front-end mockup covering: the Login/Crear Cuenta screen, the full Usuario dashboard (TopBar, 4 request buttons + their modals, "Ver mi Perfil" panel, Buscar Perfil tab), and the full Administrador dashboard (TopBar, default Solicitudes Pendientes view, Administrar Perfiles view, Logs view, Últimas Solicitudes view) — plus the three reusable modal templates (Confirmation, Form, Error). Maintain the lighter blue/red status language and the DNA-helix watermark throughout, and ensure every list, card, and panel reflects exactly the data fields defined in the data model above.
