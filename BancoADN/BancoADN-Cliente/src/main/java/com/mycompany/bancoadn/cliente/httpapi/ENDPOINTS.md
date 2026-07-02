# API Endpoints

## Auth
- **POST /api/auth/login**
  - Description: Authenticate a user.
  - Request Body:
    ```json
    {
      "email": "user@example.com",
      "password": "secret"
    }
    ```
  - Response (200 OK): JSON object representing `CuentaPersonal` with fields:
    - `idCuenta` (int, always -1 as not provided by login)
    - `nombreCuenta` (string, derived from email prefix)
    - `email` (string)
    - `idRol` (int: 1 for user, 2 for admin)
  - Error Responses:
    - 400 Bad Request if missing email/password.
    - 401 Unauthorized (implicitly via null) if credentials invalid (response "-1").

- **POST /api/auth/crear-cuenta**
  - Description: Create a new account.
  - Request Body:
    ```json
    {
      "email": "user@example.com",
      "nombreCuenta": "Juan Perez",
      "password": "secret"
    }
    ```
  - Response:
    - 200 OK with JSON `{ "success": true, "message": "Account created successfully" }` if success.
    - 400 Bad Request with JSON `{ "success": false, "message": "Email already registered" }` if duplicate email.
    - 500 Internal Server Error for other errors.

## Solicitudes (Requests)
- **POST /api/solicitudes/registrar**
  - Description: Submit a new profile registration request.
  - Headers: None
  - Request Body (`RegistrarRequest`):
    ```json
    {
      "email": "requester@example.com",
      "nombrePerfil": "Juan Perez",
      "codigoSecuencia": "ATGCGT",
      "descripcion": "Sample description",
      "fechaMuestra": "2024-01-15"
    }
    ```
  - Response (200 OK): `RegistrarResponse` JSON `{ "success": true, "message": "Request submitted successfully" }`.
  - Error: 500 if submission fails.

- **POST /api/solicitudes/modificar**
  - Description: Submit a profile modification request.
  - Request Body (`ModificarRequest` – same fields as `RegistrarRequest`).
  - Response: same as above.

- **POST /api/solicitudes/baja**
  - Description: Submit a profile deactivation request.
  - Request Body (`BajaRequest`):
    ```json
    {
      "email": "requester@example.com"
    }
    ```
  - Response: `RegistrarResponse`.

- **POST /api/solicitudes/restaurar**
  - Description: Submit a profile reactivation request.
  - Request Body (`RestaurarRequest`):
    ```json
    {
      "email": "requester@example.com"
    }
    ```
  - Response: `RegistrarResponse`.

- **GET /api/solicitudes/pendientes**
  - Description: Get pending requests for the authenticated user.
  - Headers:
    - `X-User-Email`: email of the user making the request (required).
  - Response (200 OK): JSON array of `SolicitudSummary` objects.
    Each object contains:
    - `idSolicitud` (int)
    - `tipo` (string: "REGISTRAR", "MODIFICAR", "BAJA", "RESTAURAR")
    - `estado` (int: 0 = pending)
    - `email` (string): email of the requester (taken from `X-User-Email` header)
    - `fecha` (string): request creation date
    - `nombreCompleto` (string): from request data (for REGISTRAR/MODIFICAR)
    - `codigoSequencia` (string): from request data
    - `descripcion` (string): from request data
    - `fechaMuestra` (string): from request data
  - Error: 400 if header missing.

- **POST /api/solicitudes/{id}/aprobar**
  - Description: Approve a pending request (admin only).
  - Headers:
    - `X-Admin-Email`: email of the admin approving (required)
  - Path Parameter: `id` (int) – request ID.
  - Response (200 OK): `RegistrarResponse` with success if operation succeeded.
  - Error: 400 if missing header or invalid ID.

- **POST /api/solicitudes/{id}/rechazar**
  - Description: Reject a pending request (admin only).
  - Headers: `X-Admin-Email` (required)
  - Path Parameter: `id` (int)
  - Response: same as approve.

- **GET /api/solicitudes/ultimas**
  - Description: Get recent resolved (approved/rejected) requests for the authenticated user.
  - Headers: `X-User-Email` (required)
  - Response: JSON array of `SolicitudSummary` (same structure as pendientes, but `estado` is 1 or 2).

## Perfiles
- **GET /api/perfiles/me**
  - Description: Get the profile of the authenticated user.
  - Headers: `X-User-Email` (required)
  - Response (200 OK): `PerfilDto` JSON:
    ```json
    {
      "idPerfil": 123,
      "nombreCompleto": "Juan Perez",
      "codigoSecuencia": "ATGCGT",
      "descripcion": "Sample description",
      "estado": 1,
      "fechaMuestra": "2024-01-15",
      "idCuenta": 456
    }
    ```
  - Error: 404 if profile not found.

- **GET /api/perfiles/buscar**
  - Description: Search profiles by ID or name.
  - Query Parameters:
    - `tipo`: "ID" or "Nombre"
    - `texto`: search term (or empty/null for "all")
  - Headers:
    - `X-Admin-Email` (optional): if present and non-empty, the requester is treated as admin and all results are returned; otherwise only active profiles (`estado = 1`) are returned.
  - Response (200 OK): JSON array of `PerfilDto` objects (same structure as above).

- **POST /api/perfiles/{id}/modificar**
  - Description: Update a profile (admin only).
  - Headers: `X-Admin-Email` (required)
  - Path Parameter: `id` (int) – profile ID.
  - Request Body: `PerfilDto` (fields `nombreCompleto`, `codigoSecuencia`, `descripcion`, `fechaMuestra`, `estado`; `idPerfil` is ignored and taken from path).
  - Response: 200 OK with message "Profile updated successfully" on success; 400 Bad Request on failure.

- **POST /api/perfiles/{id}/baja**
  - Description: Deactivate a profile (admin only).
  - Headers: `X-Admin-Email` (required)
  - Path Parameter: `id` (int)
  - Response: 200 OK with message "Profile deactivated successfully" on success; 400 Bad Request on failure.

- **POST /api/perfiles/{id}/restaurar**
  - Description: Reactivate a profile (admin only).
  - Headers: `X-Admin-Email` (required)
  - Path Parameter: `id` (int)
  - Response: 200 OK with message "Profile reactivated successfully" on success; 400 Bad Request on failure.

## Logs
- **GET /api/logs**
  - Description: Retrieve log entries (most recent 30).
  - Headers: None (no authentication required for this endpoint).
  - Response (200 OK): JSON array of `LogDto` objects.
    Each object contains:
    - `idRegistro` (int)
    - `fecha` (string)
    - `nombreCuenta` (string)
    - `email` (string)
    - `descripcion` (string)
    - `acciones` (string)
    - `esAdmin` (boolean)
  - Error: 500 if unable to fetch logs.