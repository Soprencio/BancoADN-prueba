# BancoADN HTTP API Endpoints

Base URL: `http://localhost:7000/api`

All endpoints return JSON unless otherwise specified.

## Authentication

This API does not implement its own authentication mechanism. Instead, it relies on headers to identify the user making the request.

### Headers
- `X-User-Email`: The email of the user making the request (required for user-specific endpoints)
- `X-Admin-Email`: The email of the administrator performing an action (required for admin-only endpoints)

## Endpoints

### Authentication

#### `POST /auth/login`
Authenticate a user and return user information.

**Request Body**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Success Response**
- **Code**: 200 OK
- **Content**:
```json
{
  "idCuenta": 123,
  "nombreCuenta": "John Doe",
  "email": "user@example.com",
  "idRol": 1
}
```

**Error Responses**
- **Code**: 400 Bad Request – Missing or invalid email/password
- **Code**: 401 Unauthorized – Invalid credentials
- **Code**: 500 Internal Server Error – Server error

#### `POST /auth/crear-cuenta`
Create a new user account.

**Request Body**
```json
{
  "nombreCuenta": "John Doe",
  "email": "user@example.com",
  "password": "password123"
}
```

**Success Response**
- **Code**: 201 Created
- **Content**: `{ "message": "Account created successfully" }`

**Error Responses**
- **Code**: 400 Bad Request – Missing or invalid fields
- **Code**: 409 Conflict – Email already registered
- **Code**: 500 Internal Server Error – Server error

### Requests (Solicitudes)

#### `GET /solicitudes/pendientes`
Get all pending requests (requires user authentication).

**Headers**
- `X-User-Email`: The email of the authenticated user

**Success Response**
- **Code**: 200 OK
- **Content**: Array of request objects
```json
[
  {
    "idSolicitud": 1,
    "tipo": "REGISTRAR",
    "estado": 0,
    "datosSolicitud": {
      "nombreCompleto": "John Doe",
      "codigoSecuencia": "ABC123",
      "fechaMuestra": "2023-01-15",
      "descripcion": "Sample description",
      "email": "user@example.com"
    }
  }
]
```

**Error Responses**
- **Code**: 400 Bad Request – Missing X-User-Email header
- **Code**: 500 Internal Server Error – Server error

#### `GET /solicitudes/ultimas`
Get the most recent resolved requests (requires user authentication).

**Headers**
- `X-User-Email`: The email of the authenticated user

**Success Response**
- **Code**: 200 OK
- **Content**: Array of request objects (same format as above)

#### `POST /solicitudes/registrar`
Submit a new registration request.

**Headers**
- `X-User-Email`: The email of the user making the request

**Request Body**
```json
{
  "nombreCompleto": "John Doe",
  "codigoSecuencia": "ABC123",
  "fechaMuestra": "2023-01-15",
  "descripcion": "Sample description"
}
```

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Request submitted successfully" }`

**Error Responses**
- **Code**: 400 Bad Request – Missing or invalid fields
- **Code**: 500 Internal Server Error – Server error

#### `POST /solicitudes/modificar`
Submit a modification request.

**Headers**
- `X-User-Email`: The email of the user making the request

**Request Body**
```json
{
  "nombreCompleto": "John Doe",
  "codigoSecuencia": "XYZ789",
  "fechaMuestra": "2023-01-15",
  "descripcion": "Updated description"
}
```

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Modification request submitted successfully" }`

#### `POST /solicitudes/baja`
Submit a request to deactivate a profile.

**Headers**
- `X-User-Email`: The email of the user making the request

**Request Body**
```json
{
  // No body required
}
```

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Deactivation request submitted successfully" }`

#### `POST /solicitudes/restaurar`
Submit a request to reactivate a profile.

**Headers**
- `X-User-Email`: The email of the user making the request

**Request Body**
```json
{
  // No body required
}
```

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Reactivation request submitted successfully" }`

#### `POST /solicitudes/{id}/aprobar`
Approve a pending request (requires admin authentication).

**Headers**
- `X-Admin-Email`: The email of the administrator

**Path Parameter**
- `id`: The ID of the request to approve

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Request approved successfully" }`

**Error Responses**
- **Code**: 400 Bad Request – Missing X-Admin-Header or invalid ID
- **Code**: 404 Not Found – Request not found
- **Code**: 500 Internal Server Error – Server error

#### `POST /solicitudes/{id}/rechazar`
Reject a pending request (requires admin authentication).

**Headers**
- `X-Admin-Email`: The email of the administrator

**Path Parameter**
- `id`: The ID of the request to reject

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Request rejected successfully" }`

### Profiles

#### `GET /perfiles/me`
Get the profile of the authenticated user.

**Headers**
- `X-User-Email`: The email of the authenticated user

**Success Response**
- **Code**: 200 OK
- **Content**:
```json
{
  "idPerfil": 456,
  "nombreCompleto": "John Doe",
  "codigoSecuencia": "ABC123",
  "descripcion": "Sample description",
  "fechaMuestra": "2023-01-15",
  "estado": 1
}
```

**Error Responses**
- **Code**: 400 Bad Request – Missing X-User-Email header
- **Code**: 404 Not Found – Profile not found
- **Code**: 500 Internal Server Error – Server error

#### `GET /perfiles/buscar`
Search for profiles by criteria.

**Query Parameters**
- `tipo`: The type of search (`ID`, `Nombre`, or `Todos`)
- `texto`: The text to search for (ignored when `tipo` is `Todos`)

**Success Response**
- **Code**: 200 OK
- **Content**: Array of profile objects (same format as above)

**Error Responses**
- **Code**: 400 Bad Request – Missing or invalid query parameters
- **Code**: 500 Internal Server Error – Server error

#### `POST /perfiles/{id}/modificar`
Update a profile (admin only).

**Headers**
- `X-Admin-Email`: The email of the administrator

**Path Parameter**
- `id`: The ID of the profile to update

**Request Body**
```json
{
  "nombreCompleto": "John Doe",
  "codigoSecuencia": "XYZ789",
  "fechaMuestra": "2023-01-15",
  "descripcion": "Updated description",
  "estado": 1
}
```

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Profile updated successfully" }`

**Error Responses**
- **Code**: 400 Bad Request – Missing X-Admin-Header, invalid ID, or invalid request body
- **Code**: 500 Internal Server Error – Server error

#### `POST /perfiles/{id}/baja`
Deactivate a profile (admin only).

**Headers**
- `X-Admin-Email`: The email of the administrator

**Path Parameter**
- `id`: The ID of the profile to deactivate

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Profile deactivated successfully" }`

**Error Responses**
- **Code**: 400 Bad Request – Missing X-Admin-Header or invalid ID
- **Code**: 500 Internal Server Error – Server error

#### `POST /perfiles/{id}/restaurar`
Reactivate a profile (admin only).

**Headers**
- `X-Admin-Email`: The email of the administrator

**Path Parameter**
- `id`: The ID of the profile to reactivate

**Success Response**
- **Code**: 200 OK
- **Content**: `{ "message": "Profile reactivated successfully" }`

**Error Responses**
- **Code**: 400 Bad Request – Missing X-Admin-Header or invalid ID
- **Code**: 500 Internal Server Error – Server error

### Logs

#### `GET /logs`
Get all log entries (requires admin authentication?).

**Headers**
- *(None required currently, but may require admin privileges in the future)*

**Success Response**
- **Code**: 200 OK
- **Content**: Array of log objects
```json
[
  {
    "idRegistro": 1,
    "fecha": "2023-01-15 10:30:00",
    "detalle": "User john.doe@example.com logged in"
  }
]
```

**Error Responses**
- **Code**: 500 Internal Server Error – Server error

## Data Types

### Request Object
- `idSolicitud` (int): The unique ID of the request
- `tipo` (string): The type of request (`REGISTRAR`, `MODIFICAR`, `BAJA`, `RESTAURAR`)
- `estado` (int): The status of the request (0 = Pending, 1 = Approved, 2 = Rejected)
- `datosSolicitud` (object): The data associated with the request (only present for `REGISTRAR` and `MODIFICAR` types)
  - `nombreCompleto` (string): Full name
  - `codigoSecuencia` (string): Sequence code
  - `fechaMuestra` (string): Sample date in `YYYY-MM-DD` format
  - `descripcion` (string): Description
  - `email` (string): Email of the user who made the request

### Profile Object
- `idPerfil` (int): The unique ID of the profile
- `nombreCompleto` (string): Full name
- `codigoSecuencia` (string): Sequence code
- `descripcion` (string): Description
- `fechaMuestra` (string): Sample date in `YYYY-MM-DD` format
- `estado` (int): Status (`1` = Active, `0` = Inactive)

### Log Object
- `idRegistro` (int): The unique ID of the log entry
- `fecha` (string): Timestamp of the log entry
- `detalle` (string): Description of the event

## Notes
- All date strings should be in `YYYY-MM-DD` format unless otherwise specified.
- String values should be UTF-8 encoded.
- The API does not currently implement HTTPS; it is intended for local development only.
- Error responses may include a JSON body with an `error` field containing a descriptive message.