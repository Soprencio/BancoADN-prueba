package com.mycompany.bancoadn.cliente.httpapi.endpoint;

import com.mycompany.bancoadn.cliente.httpapi.dto.PerfilDto;
import com.mycompany.bancoadn.cliente.httpapi.service.PerfilService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.Handler;

import java.util.List;

/**
 * Endpoints for profile operations.
 */
public class PerfilEndpoints {

    /**
     * Register profile-related endpoints.
     * @param app the Javalin application
     */
    public static void register(io.javalin.Javalin app) {
        // GET /api/perfiles/me - Get the profile of the authenticated user
        app.get("/api/perfiles/me", getPerfilByEmail);

        // GET /api/perfiles/buscar - Search for profiles by criteria
        app.get("/api/perfiles/buscar", buscarPerfiles);

        // POST /api/perfiles/{id}/modificar - Update a profile (admin)
        app.post("/api/perfiles/{id}/modificar", actualizarPerfil);

        // POST /api/perfiles/{id}/baja - Deactivate a profile (admin)
        app.post("/api/perfiles/{id}/baja", darBajaPerfil);

        // POST /api/perfiles/{id}/restaurar - Reactivate a profile (admin)
        app.post("/api/perfiles/{id}/restaurar", restaurarPerfil);
    }

    // Handler for GET /api/perfiles/me
    private static final Handler getPerfilByEmail = ctx -> {
        String email = ctx.header("X-User-Email");
        if (email == null || email.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-User-Email header");
            return;
        }

        var perfil = PerfilService.getPerfilByEmail(email);
        if (perfil == null) {
            ctx.status(HttpStatus.NOT_FOUND).result("Profile not found");
            return;
        }

        ctx.json(PerfilDto.fromModel(perfil));
    };

    // Handler for GET /api/perfiles/buscar
    private static final Handler buscarPerfiles = ctx -> {
        String tipo = ctx.queryParam("tipo");
        String texto = ctx.queryParam("texto");

        if (tipo == null || texto == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing 'tipo' or 'texto' query parameters");
            return;
        }

        String adminEmail = ctx.header("X-Admin-Email");
        boolean esAdmin = adminEmail != null && !adminEmail.isEmpty();
        // Fallback to user email if admin header not present (for normal users browsing profiles)
        String callerEmail = esAdmin ? adminEmail : ctx.header("X-User-Email");
        if (callerEmail == null || callerEmail.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-Admin-Email or X-User-Email header");
            return;
        }

        List<PerfilDto> perfiles = PerfilService.buscarPerfiles(tipo, texto, esAdmin, callerEmail)
                .stream()
                .map(PerfilDto::fromModel)
                .toList();

        ctx.json(perfiles);
    };

    // Handler for POST /api/perfiles/{id}/modificar
    private static final Handler actualizarPerfil = ctx -> {
        String idStr = ctx.pathParam("id");
        String adminEmail = ctx.header("X-Admin-Email");
        if (adminEmail == null || adminEmail.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-Admin-Email header");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid profile ID");
            return;
        }

        PerfilDto dto = ctx.bodyAsClass(PerfilDto.class);
        if (dto == null) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid request body");
            return;
        }

        // Ensure the ID from the path matches the DTO (if provided)
        dto.setIdPerfil(id);

        boolean success = PerfilService.actualizarPerfil(
                id,
                dto.getNombreCompleto(),
                dto.getCodigoSecuencia(),
                dto.getDescripcion(),
                dto.getFechaMuestra(),
                dto.getEstado(),
                adminEmail
        );

        if (success) {
            ctx.status(HttpStatus.OK).result("Profile updated successfully");
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).result("Failed to update profile");
        }
    };

    // Handler for POST /api/perfiles/{id}/baja
    private static final Handler darBajaPerfil = ctx -> {
        String idStr = ctx.pathParam("id");
        String adminEmail = ctx.header("X-Admin-Email");
        if (adminEmail == null || adminEmail.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-Admin-Email header");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idStr);
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid profile ID");
            return;
        }

        boolean success = PerfilService.deactivatePerfil(id, adminEmail);
        if (success) {
            ctx.status(HttpStatus.OK).result("Profile deactivated successfully");
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).result("Failed to deactivate profile");
        }
    };

    // Handler for POST /api/perfiles/{id}/restaurar
    private static final Handler restaurarPerfil = ctx -> {
        String idStr = ctx.pathParam("id");
        String adminEmail = ctx.header("X-Admin-Email");
        if (adminEmail == null || adminEmail.isEmpty()) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Missing X-Admin-Email header");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(ctx.pathParam("id"));
        } catch (NumberFormatException e) {
            ctx.status(HttpStatus.BAD_REQUEST).result("Invalid profile ID");
            return;
        }

        boolean success = PerfilService.activatePerfil(id, adminEmail);
        if (success) {
            ctx.status(HttpStatus.OK).result("Profile reactivated successfully");
        } else {
            ctx.status(HttpStatus.BAD_REQUEST).result("Failed to reactivate profile");
        }
    };
}