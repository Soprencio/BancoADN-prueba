package com.mycompany.bancoadn.cliente.httpapi.endpoint;

import com.mycompany.bancoadn.cliente.httpapi.dto.SolicitudDto;
import com.mycompany.bancoadn.cliente.httpapi.service.SolicitudService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.Handler;

import java.util.List;

/**
 * Endpoints for solicitud (request) operations.
 */
public class SolicitudEndpoints {

    public static void register(io.javalin.Javalin app) {
        // POST /api/solicitudes/registrar - Submit a new registration request
        app.post("/api/solicitudes/registrar", registrarRequestHandler);

        // POST /api/solicitudes/modificar - Submit a modification request
        app.post("/api/solicitudes/modificar", modificarRequestHandler);

        // POST /api/solicitudes/baja - Submit a deactivation request
        app.post("/api/solicitudes/baja", bajaRequestHandler);

        // POST /api/solicitudes/restaurar - Submit a reactivation request
        app.post("/api/solicitudes/restaurar", restaurarRequestHandler);

        // GET /api/solicitudes/pendientes - Get pending requests (user)
        app.get("/api/solicitudes/pendientes", getPendingRequestsHandler);

        // GET /api/solicitudes/ultimas - Get recent resolved requests (user)
        app.get("/api/solicitudes/ultimas", getRecentRequestsHandler);

        // POST /api/solicitudes/{id}/aprobar - Approve a pending request (admin)
        app.post("/api/solicitudes/{id}/aprobar", approveRequestHandler);

        // POST /api/solicitudes/{id}/rechazar - Reject a pending request (admin)
        app.post("/api/solicitudes/{id}/rechazar", rejectRequestHandler);
    }

    // Handler for POST /api/solicitudes/registrar
    private static final Handler registrarRequestHandler = ctx -> {
        SolicitudDto.RegistrarRequest req = ctx.bodyAsClass(SolicitudDto.RegistrarRequest.class);
        if (req == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request body");
            return;
        }
        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing email in request body");
            return;
        }

        boolean success = SolicitudService.solicitarRegistrar(
                email,
                req.getNombreCompleto(),
                req.getCodigoSecuencia(),
                req.getDescripcion(),
                req.getFechaMuestra()
        );

        if (success) {
            ctx.json(new SolicitudDto.RegistrarResponse(true, "Request submitted successfully"));
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(new SolicitudDto.RegistrarResponse(false, "Failed to submit request"));
        }
    };

    // Handler for POST /api/solicitudes/modificar
    private static final Handler modificarRequestHandler = ctx -> {
        SolicitudDto.ModificarRequest req = ctx.bodyAsClass(SolicitudDto.ModificarRequest.class);
        if (req == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request body");
            return;
        }
        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing email in request body");
            return;
        }

        boolean success = SolicitudService.solicitarModificar(
                email,
                req.getNombreCompleto(),
                req.getCodigoSecuencia(),
                req.getDescripcion(),
                req.getFechaMuestra()
        );

        if (success) {
            ctx.json(new SolicitudDto.RegistrarResponse(true, "Modification request submitted"));
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(new SolicitudDto.RegistrarResponse(false, "Failed to submit modification request"));
        }
    };

    // Handler for POST /api/solicitudes/baja
    private static final Handler bajaRequestHandler = ctx -> {
        SolicitudDto.BajaRequest req = ctx.bodyAsClass(SolicitudDto.BajaRequest.class);
        if (req == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request body");
            return;
        }
        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing email in request body");
            return;
        }

        boolean success = SolicitudService.solicitarBaja(email);
        if (success) {
            ctx.json(new SolicitudDto.RegistrarResponse(true, "Deactivation request submitted"));
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(new SolicitudDto.RegistrarResponse(false, "Failed to submit deactivation request"));
        }
    };

    // Handler for POST /api/solicitudes/restaurar
    private static final Handler restaurarRequestHandler = ctx -> {
        SolicitudDto.RestaurarRequest req = ctx.bodyAsClass(SolicitudDto.RestaurarRequest.class);
        if (req == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request body");
            return;
        }
        String email = req.getEmail();
        if (email == null || email.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing email in request body");
            return;
        }

        boolean success = SolicitudService.solicitarRestaurar(email);
        if (success) {
            ctx.json(new SolicitudDto.RegistrarResponse(true, "Reactivation request submitted"));
        } else {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .json(new SolicitudDto.RegistrarResponse(false, "Failed to submit reactivation request"));
        }
    };

    // Handler for GET /api/solicitudes/pendientes
    private static final Handler getPendingRequestsHandler = ctx -> {
        String email = ctx.header("X-User-Email");
        if (email == null || email.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-User-Email header");
            return;
        }

                List<SolicitudDto.SolicitudSummary> pending = SolicitudService.getPendingRequests(email)
                .stream()
                .map(solicitud -> {
                    String tipo = solicitud.getTipo();
                    String datos = solicitud.getDatosSolicitud();
                    int idPerfil = solicitud.getIdPerfil();

                    // For BAJA/RESTAURAR, email must be resolved via EmailPorPerfil
                    // (identical to Ctrl_MenuAdmin.obtenerEmailPorPerfil() in old Swing)
                    String requesterEmail;
                    String nombreCuenta;
                    if ("baja".equalsIgnoreCase(tipo) || "restaurar".equalsIgnoreCase(tipo)) {
                        requesterEmail = idPerfil > 0 ? SolicitudService.obtenerEmailPorPerfil(email, idPerfil) : null;
                        nombreCuenta = null;
                    } else {
                        // REGISTRAR/MODIFICAR: datosSolicitud = "email _ nombre _ codigoSec _ descripcion _ fecha"
                        requesterEmail = extractEmail(datos);
                        nombreCuenta = extractNombreCompleto(datos); // Person's name from solicitud
                    }

                    return new SolicitudDto.SolicitudSummary(
                            solicitud.getIdSolicitud(),
                            solicitud.getTipo(),
                            solicitud.getEstado(),
                            requesterEmail,
                            solicitud.getFechaCreacion(),
                            extractNombreCompleto(datos),
                            extractCodigoSecuencia(datos),
                            extractDescripcion(datos),
                            extractFechaMuestra(datos),
                            nombreCuenta
                    );
                })
                .toList();

        ctx.json(pending);
    };

    // Helper methods to extract data from the datosSolicitud string (format: "email _ nombreCompleto _ codigoSecuencia _ descripcion _ fechaMuestra")
    private static String extractEmail(String datos) {
        if (datos == null) return null;
        String[] parts = datos.split(" _ ", -1);
        return parts.length > 0 ? parts[0].trim() : null;
    }

    private static String extractNombreCompleto(String datos) {
        if (datos == null) return null;
        String[] parts = datos.split(" _ ", -1);
        return parts.length > 1 ? parts[1].trim() : null;
    }

    private static String extractCodigoSecuencia(String datos) {
        if (datos == null) return null;
        String[] parts = datos.split(" _ ", -1);
        return parts.length > 2 ? parts[2].trim() : null;
    }

    private static String extractDescripcion(String datos) {
        if (datos == null) return null;
        String[] parts = datos.split(" _ ", -1);
        return parts.length > 3 ? parts[3].trim() : null;
    }

    private static String extractFechaMuestra(String datos) {
        if (datos == null) return null;
        String[] parts = datos.split(" _ ", -1);
        return parts.length > 4 ? parts[4].trim() : null;
    }

    // Handler for GET /api/solicitudes/ultimas
    private static final Handler getRecentRequestsHandler = ctx -> {
        String email = ctx.header("X-User-Email");
        if (email == null || email.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-User-Email header");
            return;
        }

                List<SolicitudDto.SolicitudSummary> recientes = SolicitudService.getRecentRequests(email)
                .stream()
                .map(solicitud -> {
                    String tipo = solicitud.getTipo();
                    String datos = solicitud.getDatosSolicitud();
                    int idPerfil = solicitud.getIdPerfil();

                    // For BAJA/RESTAURAR, email must be resolved via EmailPorPerfil
                    String requesterEmail;
                    String nombreCuenta;
                    if ("baja".equalsIgnoreCase(tipo) || "restaurar".equalsIgnoreCase(tipo)) {
                        requesterEmail = SolicitudService.obtenerEmailPorPerfil(email, idPerfil);
                        nombreCuenta = null;
                    } else {
                        requesterEmail = extractEmail(datos);
                        nombreCuenta = extractNombreCompleto(datos);
                    }

                    return new SolicitudDto.SolicitudSummary(
                            solicitud.getIdSolicitud(),
                            solicitud.getTipo(),
                            solicitud.getEstado(),
                            requesterEmail,
                            solicitud.getFechaCreacion(),
                            extractNombreCompleto(datos),
                            extractCodigoSecuencia(datos),
                            extractDescripcion(datos),
                            extractFechaMuestra(datos),
                            nombreCuenta
                    );
                })
                .toList();

        ctx.json(recientes);
    };

    // Handler for POST /api/solicitudes/{id}/aprobar
    private static final Handler approveRequestHandler = ctx -> {
        String adminEmail = ctx.header("X-Admin-Email");
        if (adminEmail == null || adminEmail.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-Admin-Email header");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request ID");
            return;
        }

        boolean success = SolicitudService.approveRequest(id, adminEmail);
        if (success) {
            ctx.json(new SolicitudDto.RegistrarResponse(true, "Request approved"));
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).json(new SolicitudDto.RegistrarResponse(false, "Failed to approve request"));
        }
    };

    // Handler for POST /api/solicitudes/{id}/rechazar
    private static final Handler rejectRequestHandler = ctx -> {
        String adminEmail = ctx.header("X-Admin-Email");
        if (adminEmail == null || adminEmail.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-Admin-Email header");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request ID");
            return;
        }

        boolean success = SolicitudService.rejectRequest(id, adminEmail);
        if (success) {
            ctx.json(new SolicitudDto.RegistrarResponse(true, "Request rejected"));
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).json(new SolicitudDto.RegistrarResponse(false, "Failed to reject request"));
        }
    };
}