package com.mycompany.bancoadn.cliente.httpapi;

import io.javalin.Javalin;
import com.mycompany.bancoadn.cliente.httpapi.BridgeEndpoints;

/** Servidor HTTP API usando Javalin */
public class ApiServer {

    private Javalin app;

    /**
     * Start del server HTTP con el puerto pertinente
     * @param port the port to listen on
     */
    public void start(int port) {
        app = Javalin.create().start(port);

        // Permite CORS (Encuentra el puerto del frontend, generalmentw 5173 pero puede variar)
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

        // Registra bridge endpoints
        BridgeEndpoints.register(app);

        app.get("/", ctx -> {
            ctx.result("BancoADN HTTP API is running");
        });

        System.out.println("HTTP server started on port " + port);
    }

    /**
     * Stop del server HTTP.
     */
    public void stop() {
        if (app != null) {
            app.stop();
        }
    }
}