package com.mycompany.bancoadn.cliente.httpapi;

import io.javalin.Javalin;
import com.mycompany.bancoadn.cliente.httpapi.BridgeEndpoints;

/**
 * HTTP API server using Javalin.
 * Exposes the existing socket-based functionality as REST endpoints.
 */
public class ApiServer {

    private Javalin app;

    /**
     * Starts the HTTP server on the specified port.
     * @param port the port to listen on
     */
    public void start(int port) {
        app = Javalin.create().start(port);

        // Enable CORS for any React frontend origin (development)
        app.before(ctx -> {
            String origin = ctx.header("Origin");
            if (origin != null && !origin.isEmpty()) {
                ctx.header("Access-Control-Allow-Origin", origin);
                ctx.header("Access-Control-Allow-Credentials", "true");
            }
            if (ctx.method() == io.javalin.http.HandlerType.OPTIONS) {
                ctx.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
                ctx.header("Access-Control-Allow-Headers", "Content-Type, X-User-Email, X-Admin-Email");
                ctx.result("");
                ctx.status(200);
                ctx.skipRemainingHandlers();
            }
        });

        // Register bridge endpoints (controllers via HTTP bridges)
        BridgeEndpoints.register(app);

        // Root endpoint for health check
        app.get("/", ctx -> {
            ctx.result("BancoADN HTTP API is running");
        });

        System.out.println("HTTP server started on port " + port);
    }

    /**
     * Stops the HTTP server.
     */
    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}