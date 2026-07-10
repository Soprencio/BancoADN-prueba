package com.mycompany.bancoadn.cliente.httpapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_ClienteSocket;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_IniciarSesion;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_CrearCuenta;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_MenuUsuario;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_MenuAdmin;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_BuscarPerfil;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_SolicitarPerfil;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_SolicitarModPerfil;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_AdminPerfiles;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_ModificarPerfilAdmin;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_Logs;
import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_Ctrl_UltimasSolicitudes;
import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.httpapi.bridge.*;
import com.mycompany.bancoadn.cliente.httpapi.service.SessionSocket;
import io.javalin.Javalin;
import java.util.List;
import java.util.Map;

public class BridgeEndpoints {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void register(Javalin app) {

        // ════════════════════════════════════════════════════════
        //  AUTH
        // ════════════════════════════════════════════════════════

        app.post("/api/auth/login", ctx -> {
            Map<String, String> body = MAPPER.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
            String email = body.getOrDefault("email", "");
            String password = body.getOrDefault("password", "");

            BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
            if (!socket.estaConectado()) {
                ctx.status(401).json(Map.of("message", "No se pudo conectar al servidor"));
                return;
            }

            VistaIniciarSesionBridge bridge = new VistaIniciarSesionBridge();
            bridge.setInputEmail(email);
            bridge.setInputContraseña(password);

            BancoADN_Grupo6_Ctrl_IniciarSesion ctrl = new BancoADN_Grupo6_Ctrl_IniciarSesion(bridge, socket);
            ctrl.ejecutarLogin(email, password);

            BridgeResult br = bridge.getResult();
            String nav = (String) br.get("navigate");

            if ("menuUsuario".equals(nav) || "menuAdmin".equals(nav)) {
                SessionSocket.setSocket(socket, email);
                String nombre = (String) br.get("nombre");
                int idRol = nav.equals("menuAdmin") ? 2 : 1;
                ctx.json(Map.of(
                    "idCuenta", -1,
                    "nombreCuenta", nombre != null ? nombre : "",
                    "email", email,
                    "idRol", idRol
                ));
            } else {
                String msg = (String) br.get("message");
                ctx.status(401).json(Map.of("message", msg != null ? msg : "Credenciales incorrectas"));
            }
        });

        app.post("/api/auth/logout", ctx -> {
            SessionSocket.close();
            ctx.result("Logged out");
        });

        app.post("/api/auth/crear-cuenta", ctx -> {
            Map<String, String> body = MAPPER.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
            String nombre = body.getOrDefault("nombreCuenta", "");
            String email = body.getOrDefault("email", "");
            String password = body.getOrDefault("password", "");

            BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
            if (!socket.estaConectado()) {
                ctx.status(400).json(Map.of("message", "No se pudo conectar al servidor"));
                return;
            }

            VistaCrearCuentaBridge bridge = new VistaCrearCuentaBridge();
            bridge.setInputNombre(nombre);
            bridge.setInputEmail(email);
            bridge.setInputContraseña(password);

            BancoADN_Grupo6_Ctrl_CrearCuenta ctrl = new BancoADN_Grupo6_Ctrl_CrearCuenta(bridge, socket);
            ctrl.ejecutarCrearCuenta(nombre, email, password);

            BridgeResult br = bridge.getResult();
            String nav = (String) br.get("navigate");
            if ("login".equals(nav)) {
                ctx.status(201).json(Map.of(
                    "idCuenta", -1,
                    "nombreCuenta", nombre,
                    "email", email,
                    "idRol", 1
                ));
            } else {
                String msg = (String) br.get("message");
                ctx.status(400).json(Map.of("message", msg != null ? msg : "Error al crear cuenta"));
            }
        });

        // ════════════════════════════════════════════════════════
        //  CUENTAS (stub – never truly implemented in old backend)
        // ════════════════════════════════════════════════════════

        app.get("/api/cuentas", ctx -> {
            ctx.json(List.of());
        });

        // ════════════════════════════════════════════════════════
        //  PERFILES
        // ════════════════════════════════════════════════════════

        app.get("/api/perfiles", ctx -> {
            ctx.json(List.of());
        });

        app.get("/api/perfiles/me", ctx -> {
            String email = ctx.header("X-User-Email");
            if (email == null || email.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de usuario"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(email);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            CuentaPersonal cuenta = new CuentaPersonal(-1, email.split("@")[0], "", email, 1);
            VistaMenuUsuarioBridge bridge = new VistaMenuUsuarioBridge();

            BancoADN_Grupo6_Ctrl_MenuUsuario ctrl = new BancoADN_Grupo6_Ctrl_MenuUsuario(bridge, socket, cuenta);
            ctrl.manejarVerPerfil();

            BridgeResult br = bridge.getResult();
            Map<String, Object> perfilData = (Map<String, Object>) br.get("perfil");
            if (perfilData == null) {
                ctx.status(404).json(Map.of("message", "Perfil no encontrado"));
                return;
            }
            perfilData.put("email", email);
            ctx.json(perfilData);
        });

        app.get("/api/perfiles/buscar", ctx -> {
            String tipo = ctx.queryParam("tipo");
            String texto = ctx.queryParam("texto");
            if (tipo == null || texto == null) {
                ctx.status(400).json(Map.of("message", "Debe especificar tipo y texto"));
                return;
            }

            String callerEmail = ctx.header("X-Admin-Email");
            boolean isAdmin = callerEmail != null && !callerEmail.isEmpty();
            if (!isAdmin) callerEmail = ctx.header("X-User-Email");
            if (callerEmail == null || callerEmail.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(callerEmail);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            CuentaPersonal cuenta = new CuentaPersonal(-1, callerEmail.split("@")[0], "", callerEmail, isAdmin ? 2 : 1);

            // Normalize tipo: frontend sends lowercase ("nombre", "codigo", "todos")
            String tipoNorm;
            if (tipo.equalsIgnoreCase("nombre")) {
                tipoNorm = "Nombre";
            } else if (tipo.equalsIgnoreCase("codigo") || tipo.equalsIgnoreCase("id")) {
                tipoNorm = "ID";
            } else {
                tipoNorm = tipo;
            }

            if (isAdmin) {
                VistaAdminPerfilesBridge bridge = new VistaAdminPerfilesBridge();
                bridge.setInputTipoFiltro(tipoNorm);
                bridge.setInputTextoBusqueda(texto);
                BancoADN_Grupo6_Ctrl_AdminPerfiles ctrl = new BancoADN_Grupo6_Ctrl_AdminPerfiles(bridge, socket, cuenta);
                ctrl.ejecutarBusqueda(tipoNorm, texto);
                BridgeResult br = bridge.getResult();
                if (br.toMap().containsKey("error")) {
                    ctx.status(400).json(Map.of("message", br.get("error")));
                    return;
                }
                List<?> cards = (List<?>) br.get("perfiles");
                ctx.json(cards != null ? cards : List.of());
            } else {
                VistaBuscarPerfilBridge bridge = new VistaBuscarPerfilBridge();
                bridge.setInputTipoFiltro(tipoNorm);
                bridge.setInputTextoBusqueda(texto);
                BancoADN_Grupo6_Ctrl_BuscarPerfil ctrl = new BancoADN_Grupo6_Ctrl_BuscarPerfil(bridge, socket, cuenta);
                ctrl.ejecutarBusqueda(tipoNorm, texto);
                BridgeResult br = bridge.getResult();
                if (br.toMap().containsKey("error")) {
                    ctx.status(400).json(Map.of("message", br.get("error")));
                    return;
                }
                List<?> cards = (List<?>) br.get("resultados");
                ctx.json(cards != null ? cards : List.of());
            }
        });

        app.get("/api/perfiles/{id}", ctx -> {
            ctx.status(404).json(Map.of("message", "Perfil no encontrado"));
        });

        app.post("/api/perfiles/{id}/modificar", ctx -> {
            String adminEmail = ctx.header("X-Admin-Email");
            if (adminEmail == null || adminEmail.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de administrador"));
                return;
            }
            int perfilId = Integer.parseInt(ctx.pathParam("id"));

            Map<String, String> body = MAPPER.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
            String nombre = body.getOrDefault("nombreCompleto", "");
            String codigo = body.getOrDefault("codigoSecuencia", "");
            String desc = body.getOrDefault("descripcion", "");
            String fecha = body.getOrDefault("fechaMuestra", "");

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(adminEmail);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            String emailTitular = resolverEmailPorIdPerfil(socket, perfilId);
            if (emailTitular == null) {
                ctx.status(400).json(Map.of("message", "No se pudo determinar el titular del perfil"));
                return;
            }

            CuentaPersonal admin = new CuentaPersonal(-1, adminEmail.split("@")[0], "", adminEmail, 2);
            VistaModificarPerfilAdminBridge bridge = new VistaModificarPerfilAdminBridge();
            bridge.setInputNombre(nombre);
            bridge.setInputCodigo(codigo);
            bridge.setInputDescripcion(desc);
            bridge.setInputFecha(fecha);

            BancoADN_Grupo6_Ctrl_ModificarPerfilAdmin ctrl = new BancoADN_Grupo6_Ctrl_ModificarPerfilAdmin(bridge, socket, emailTitular, admin);
            ctrl.ejecutarModificacion(nombre, codigo, desc, fecha);

            BridgeResult br = bridge.getResult();
            String msg = (String) br.get("message");
            if (br.toMap().containsKey("success") && Boolean.FALSE.equals(br.get("success"))) {
                ctx.status(400).json(Map.of("message", msg != null ? msg : "Error al modificar perfil"));
            } else {
                ctx.json(Map.of("message", msg != null ? msg : "Perfil modificado exitosamente"));
            }
        });

        app.post("/api/perfiles/{id}/baja", ctx -> {
            String adminEmail = ctx.header("X-Admin-Email");
            if (adminEmail == null || adminEmail.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de administrador"));
                return;
            }
            int perfilId = Integer.parseInt(ctx.pathParam("id"));

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(adminEmail);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            CuentaPersonal admin = new CuentaPersonal(-1, adminEmail.split("@")[0], "", adminEmail, 2);
            VistaAdminPerfilesBridge bridge = new VistaAdminPerfilesBridge();
            bridge.setConfirmResult(true);

            BancoADN_Grupo6_Ctrl_AdminPerfiles ctrl = new BancoADN_Grupo6_Ctrl_AdminPerfiles(bridge, socket, admin);
            String emailTitular = resolverEmailPorIdPerfil(socket, perfilId);
            if (emailTitular == null) {
                ctx.status(400).json(Map.of("message", "No se pudo determinar el titular del perfil"));
                return;
            }
            ctrl.solicitarBajaPerfil(emailTitular);

            BridgeResult br = bridge.getResult();
            String msg = (String) br.get("message");
            if (br.toMap().containsKey("success") && Boolean.FALSE.equals(br.get("success"))) {
                ctx.status(400).json(Map.of("message", msg != null ? msg : "Error al dar de baja"));
            } else {
                ctx.json(Map.of("message", msg != null ? msg : "Perfil desactivado exitosamente"));
            }
        });

        app.post("/api/perfiles/{id}/restaurar", ctx -> {
            String adminEmail = ctx.header("X-Admin-Email");
            if (adminEmail == null || adminEmail.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de administrador"));
                return;
            }
            int perfilId = Integer.parseInt(ctx.pathParam("id"));

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(adminEmail);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            CuentaPersonal admin = new CuentaPersonal(-1, adminEmail.split("@")[0], "", adminEmail, 2);
            VistaAdminPerfilesBridge bridge = new VistaAdminPerfilesBridge();
            bridge.setConfirmResult(true);

            BancoADN_Grupo6_Ctrl_AdminPerfiles ctrl = new BancoADN_Grupo6_Ctrl_AdminPerfiles(bridge, socket, admin);
            String emailTitular = resolverEmailPorIdPerfil(socket, perfilId);
            if (emailTitular == null) {
                ctx.status(400).json(Map.of("message", "No se pudo determinar el titular del perfil"));
                return;
            }
            ctrl.solicitarRestaurarPerfil(emailTitular);

            BridgeResult br = bridge.getResult();
            String msg = (String) br.get("message");
            if (br.toMap().containsKey("success") && Boolean.FALSE.equals(br.get("success"))) {
                ctx.status(400).json(Map.of("message", msg != null ? msg : "Error al restaurar"));
            } else {
                ctx.json(Map.of("message", msg != null ? msg : "Perfil restaurado exitosamente"));
            }
        });

        // ════════════════════════════════════════════════════════
        //  SOLICITUDES
        // ════════════════════════════════════════════════════════

        app.get("/api/solicitudes", ctx -> {
            ctx.json(List.of());
        });

        app.get("/api/solicitudes/pendientes", ctx -> {
            String email = ctx.header("X-User-Email");
            if (email == null || email.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de usuario"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(email);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            CuentaPersonal admin = new CuentaPersonal(-1, email.split("@")[0], "", email, 2);
            VistaMenuAdminBridge bridge = new VistaMenuAdminBridge();
            BancoADN_Grupo6_Ctrl_MenuAdmin ctrl = new BancoADN_Grupo6_Ctrl_MenuAdmin(bridge, socket, admin);
            ctrl.consultarSolicitudesPendientes();

            BridgeResult br = bridge.getResult();
            List<?> cards = (List<?>) br.get("solicitudes");
            ctx.json(cards != null ? cards : List.of());
        });

        app.get("/api/solicitudes/ultimas", ctx -> {
            String email = ctx.header("X-User-Email");
            if (email == null || email.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de usuario"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(email);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            VistaUltimasSolicitudesBridge bridge = new VistaUltimasSolicitudesBridge();
            new BancoADN_Grupo6_Ctrl_UltimasSolicitudes(bridge, socket);

            BridgeResult br = bridge.getResult();
            List<?> cards = (List<?>) br.get("solicitudes");
            ctx.json(cards != null ? cards : List.of());
        });

        app.post("/api/solicitudes/registrar", ctx -> {
            String email = ctx.header("X-User-Email");
            if (email == null || email.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de usuario"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(email);
            if (socket == null) { ctx.status(401).json(Map.of("message", "Sesión no activa")); return; }

            Map<String, String> body = MAPPER.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
            CuentaPersonal cuenta = new CuentaPersonal(-1, email.split("@")[0], "", email, 1);
            VistaSolicitarPerfilBridge bridge = new VistaSolicitarPerfilBridge();
            bridge.setInputNombre(body.getOrDefault("nombreCompleto", ""));
            bridge.setInputCodigo(body.getOrDefault("codigoSecuencia", ""));
            bridge.setInputDescripcion(body.getOrDefault("descripcion", ""));
            bridge.setInputFecha(body.getOrDefault("fechaMuestra", ""));

            BancoADN_Grupo6_Ctrl_SolicitarPerfil ctrl = new BancoADN_Grupo6_Ctrl_SolicitarPerfil(bridge, socket, cuenta);
            ctrl.ejecutarSolicitud(bridge.getNombrePerfil(), bridge.getCodigoSecuencia(), bridge.getDescripcion(), bridge.getFechaMuestra());

            ctx.json(Map.of(
                "idSolicitud", -1,
                "message", "Solicitud enviada exitosamente"
            ));
        });

        app.post("/api/solicitudes/modificar", ctx -> {
            String email = ctx.header("X-User-Email");
            if (email == null || email.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de usuario"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(email);
            if (socket == null) { ctx.status(401).json(Map.of("message", "Sesión no activa")); return; }

            Map<String, String> body = MAPPER.readValue(ctx.body(), new TypeReference<Map<String, String>>() {});
            CuentaPersonal cuenta = new CuentaPersonal(-1, email.split("@")[0], "", email, 1);
            VistaSolicitarModPerfilBridge bridge = new VistaSolicitarModPerfilBridge();
            bridge.setInputNombre(body.getOrDefault("nombreCompleto", ""));
            bridge.setInputCodigo(body.getOrDefault("codigoSecuencia", ""));
            bridge.setInputDescripcion(body.getOrDefault("descripcion", ""));
            bridge.setInputFecha(body.getOrDefault("fechaMuestra", ""));

            BancoADN_Grupo6_Ctrl_SolicitarModPerfil ctrl = new BancoADN_Grupo6_Ctrl_SolicitarModPerfil(bridge, socket, cuenta);
            ctrl.ejecutarSolicitud(bridge.getNombrePerfil(), bridge.getCodigoSecuencia(), bridge.getDescripcion(), bridge.getFechaMuestra());

            ctx.json(Map.of(
                "idSolicitud", -1,
                "message", "Solicitud enviada exitosamente"
            ));
        });

        app.post("/api/solicitudes/baja", ctx -> {
            String email = ctx.header("X-User-Email");
            if (email == null || email.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de usuario"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(email);
            if (socket == null) { ctx.status(401).json(Map.of("message", "Sesión no activa")); return; }

            CuentaPersonal cuenta = new CuentaPersonal(-1, email.split("@")[0], "", email, 1);
            VistaMenuUsuarioBridge bridge = new VistaMenuUsuarioBridge();
            bridge.setConfirmResult(true);
            BancoADN_Grupo6_Ctrl_MenuUsuario ctrl = new BancoADN_Grupo6_Ctrl_MenuUsuario(bridge, socket, cuenta);
            ctrl.manejarSolicitarDesactivar();

            ctx.json(Map.of(
                "idSolicitud", -1,
                "message", "Solicitud enviada exitosamente"
            ));
        });

        app.post("/api/solicitudes/restaurar", ctx -> {
            String email = ctx.header("X-User-Email");
            if (email == null || email.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de usuario"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(email);
            if (socket == null) { ctx.status(401).json(Map.of("message", "Sesión no activa")); return; }

            CuentaPersonal cuenta = new CuentaPersonal(-1, email.split("@")[0], "", email, 1);
            VistaMenuUsuarioBridge bridge = new VistaMenuUsuarioBridge();
            bridge.setConfirmResult(true);
            BancoADN_Grupo6_Ctrl_MenuUsuario ctrl = new BancoADN_Grupo6_Ctrl_MenuUsuario(bridge, socket, cuenta);
            ctrl.manejarSolicitarReactivar();

            ctx.json(Map.of(
                "idSolicitud", -1,
                "message", "Solicitud enviada exitosamente"
            ));
        });

        app.post("/api/solicitudes/{id}/aprobar", ctx -> {
            String adminEmail = ctx.header("X-Admin-Email");
            if (adminEmail == null || adminEmail.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de administrador"));
                return;
            }
            int id = Integer.parseInt(ctx.pathParam("id"));

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(adminEmail);
            if (socket == null) { ctx.status(401).json(Map.of("message", "Sesión no activa")); return; }

            CuentaPersonal admin = new CuentaPersonal(-1, adminEmail.split("@")[0], "", adminEmail, 2);
            VistaMenuAdminBridge bridge = new VistaMenuAdminBridge();
            bridge.setConfirmResult(true);

            BancoADN_Grupo6_Ctrl_MenuAdmin ctrl = new BancoADN_Grupo6_Ctrl_MenuAdmin(bridge, socket, admin);
            ctrl.resolverSolicitud(id, 1);

            ctx.json(Map.of("idSolicitud", id, "message", "Solicitud aprobada"));
        });

        app.post("/api/solicitudes/{id}/rechazar", ctx -> {
            String adminEmail = ctx.header("X-Admin-Email");
            if (adminEmail == null || adminEmail.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de administrador"));
                return;
            }
            int id = Integer.parseInt(ctx.pathParam("id"));

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(adminEmail);
            if (socket == null) { ctx.status(401).json(Map.of("message", "Sesión no activa")); return; }

            CuentaPersonal admin = new CuentaPersonal(-1, adminEmail.split("@")[0], "", adminEmail, 2);
            VistaMenuAdminBridge bridge = new VistaMenuAdminBridge();
            bridge.setConfirmResult(true);

            BancoADN_Grupo6_Ctrl_MenuAdmin ctrl = new BancoADN_Grupo6_Ctrl_MenuAdmin(bridge, socket, admin);
            ctrl.resolverSolicitud(id, 2);

            ctx.json(Map.of("idSolicitud", id, "message", "Solicitud rechazada"));
        });

        // ════════════════════════════════════════════════════════
        //  LOGS
        // ════════════════════════════════════════════════════════

        app.get("/api/logs", ctx -> {
            String adminEmail = ctx.header("X-Admin-Email");
            if (adminEmail == null || adminEmail.isEmpty()) {
                ctx.status(400).json(Map.of("message", "Debe especificar un email de administrador"));
                return;
            }

            BancoADN_Grupo6_ClienteSocket socket = SessionSocket.getSocket(adminEmail);
            if (socket == null) {
                ctx.status(401).json(Map.of("message", "Sesión no activa"));
                return;
            }

            VistaLogsBridge bridge = new VistaLogsBridge();
            new BancoADN_Grupo6_Ctrl_Logs(bridge, socket, "ALL");

            BridgeResult br = bridge.getResult();
            List<?> cards = (List<?>) br.get("logs");
            ctx.json(cards != null ? cards : List.of());
        });
    }

    private static String resolverEmailPorIdPerfil(BancoADN_Grupo6_ClienteSocket socket, int perfilId) {
        String resp = socket.enviarYRecibir("EmailPorPerfil - " + perfilId);
        if (resp == null || resp.trim().isEmpty() || resp.equals("0") || resp.equals("-1")) return null;
        return resp.trim();
    }
}
