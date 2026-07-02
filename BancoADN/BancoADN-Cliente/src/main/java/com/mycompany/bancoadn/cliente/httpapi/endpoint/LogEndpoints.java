package com.mycompany.bancoadn.cliente.httpapi.endpoint;

import com.mycompany.bancoadn.cliente.httpapi.dto.LogDto;
import com.mycompany.bancoadn.cliente.httpapi.service.LogService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.Handler;

import java.util.List;

/**
 * Endpoints for log operations.
 */
public class LogEndpoints {

    /**
     * Register log-related endpoints.
     * @param app the Javalin application
     */
    public static void register(io.javalin.Javalin app) {
        // GET /api/logs - Get all logs
        app.get("/api/logs", getLogs);
    }

    // Handler for GET /api/logs
    private static final Handler getLogs = ctx -> {
        String adminEmail = ctx.header("X-Admin-Email");
        List<LogDto> logs;
        if (adminEmail != null && !adminEmail.isEmpty()) {
            logs = LogService.getLogs(adminEmail);
        } else {
            logs = LogService.getLogs();
        }
        if (logs == null) {
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).result("Failed to retrieve logs");
            return;
        }
        ctx.json(logs);
    };
}