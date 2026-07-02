package com.mycompany.bancoadn.cliente;

import javax.swing.JOptionPane;
import java.util.List;
import java.util.ArrayList;
import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.Solicitud;


public class BancoADN_Grupo6_Ctrl_MenuAdmin {

    private final BancoADN_Grupo6_MenuAdmin     vista;
    private final BancoADN_Grupo6_ClienteSocket clienteSocket;
    private final CuentaPersonal adminLogueado; // Credencial de sesión

    public BancoADN_Grupo6_Ctrl_MenuAdmin(BancoADN_Grupo6_MenuAdmin vista, 
                                           BancoADN_Grupo6_ClienteSocket clienteSocket, 
                                           CuentaPersonal adminLogueado) {
        this.vista         = vista;
        this.clienteSocket = clienteSocket;
        this.adminLogueado = adminLogueado;

        initListeners();
        consultarSolicitudesPendientes();
    }

    private void initListeners() {
        vista.agregarListenerCerrarSesion(e -> manejarCerrarSesion());
        vista.agregarListenerUltimasSolicitudes(e  -> abrirUltimasSolicitudes());
        vista.agregarListenerLogs(e                -> abrirLogs());
        vista.agregarListenerAdministrarPerfiles(e -> abrirAdministrarPerfiles());
    }

    // ════════════════════════════════════════════════════════
    // ── LÓGICA DE CONTROL (Alineada con Diagramas) ─────────
    // ════════════════════════════════════════════════════════

    /**
     * Carga y muestra las solicitudes pendientes usando el snapshot.
     * Corresponde a "consultarSolicitudesPendientes" en el Diagrama de Clases.
     */
    private void consultarSolicitudesPendientes() {
        vista.limpiarSolicitudes();
        List<Solicitud> solicitudes = obtenerSnapshotSolicitudes();

        if (solicitudes.isEmpty()) {
            vista.mostrarSinSolicitudes();
            return;
        }

        for (Solicitud sol : solicitudes) {
            String[] tokens = sol.getDatosSolicitud().split(" _ ", -1);

            String mailMostrar;
            String lineaExtra;

            if (sol.getTipo().equalsIgnoreCase("registrar") || sol.getTipo().equalsIgnoreCase("modificar")) {
                mailMostrar = tokens.length > 0 ? tokens[0].trim() : "—";
                lineaExtra  = tokens.length > 3 ? tokens[3].trim() : "—";
            } else {
                String emailTitular = obtenerEmailPorPerfil(sol.getIdPerfil());
                mailMostrar = emailTitular;
                lineaExtra  = emailTitular;
            }

            final int idCapturado = sol.getIdSolicitud();
            vista.agregarTarjetaSolicitud(
                sol.getIdSolicitud(),
                mailMostrar,
                sol.getFechaCreacion(),
                sol.getTipo(),
                lineaExtra,
                e -> resolverSolicitud(idCapturado, 1),
                e -> resolverSolicitud(idCapturado, 2)
            );
        }
    }

    /**
     * Punto de entrada para la resolución. 
     * Implementa el OPT(estado==0) del diagrama de secuencia mediante verificación previa.
     */
    private void resolverSolicitud(int idSolicitud, int estadoNuevo) {
        // 1. Verificación de estado (Snaphost preventivo para concurrencia)
        if (!verificarEstadoSolicitud(idSolicitud)) {
            vista.mostrarError("La solicitud #" + idSolicitud + " ya ha sido procesada por otro administrador.");
            consultarSolicitudesPendientes();
            return;
        }

        String pregunta = (estadoNuevo == 1)
            ? "¿Confirmás la APROBACIÓN de la solicitud #" + idSolicitud + "?"
            : "¿Confirmás el RECHAZO de la solicitud #"   + idSolicitud + "?";

        if (!vista.confirmar(pregunta)) return;

        // 2. Envío de resolución al servidor
        String mensaje = "ResSol - " + idSolicitud + " - " + estadoNuevo + " - " + adminLogueado.getEmail();
        String respuesta = clienteSocket.enviarYRecibir(mensaje);

        // 3. Procesar resultado
        procesarResolucion(idSolicitud, respuesta, estadoNuevo);
    }

    /**
     * Verifica si una solicitud sigue pendiente antes de actuar.
     */
    private boolean verificarEstadoSolicitud(int idSolicitud) {
        List<Solicitud> pendientes = obtenerSnapshotSolicitudes();
        for (Solicitud s : pendientes) {
            if (s.getIdSolicitud() == idSolicitud && s.isPendiente()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Maneja la respuesta del servidor y actualiza la UI.
     */
    private void procesarResolucion(int idSolicitud, String respuesta, int estadoNuevo) {
        if (respuesta == null) {
            vista.mostrarError("Sin respuesta del servidor.");
            return;
        }

        if (respuesta.trim().equals("1") || respuesta.trim().equals("0")) {
            String msg = (estadoNuevo == 1) ? "aprobada" : "rechazada";
            vista.mostrarMensaje("✓ Solicitud #" + idSolicitud + " " + msg + " exitosamente.");
        } else {
            vista.mostrarError("Error al resolver la solicitud #" + idSolicitud + ": " + respuesta);
        }

        consultarSolicitudesPendientes();
    }

    // ════════════════════════════════════════════════════════
    // ── NAVEGACIÓN ─────────────────────────────────────────
    // ════════════════════════════════════════════════════════

    private void abrirLogs() {
        BancoADN_Grupo6_Pant_Logs pantallaLogs = new BancoADN_Grupo6_Pant_Logs(true);
        new BancoADN_Grupo6_Ctrl_Logs(pantallaLogs, clienteSocket, "ALL");
        pantallaLogs.setVisible(true);
    }

    private void abrirUltimasSolicitudes() {
        BancoADN_Grupo6_Pant_UltimasSolicitudes pantallaUlt = new BancoADN_Grupo6_Pant_UltimasSolicitudes();
        new BancoADN_Grupo6_Ctrl_UltimasSolicitudes(pantallaUlt, clienteSocket);
        pantallaUlt.setVisible(true);
    }

    private void abrirAdministrarPerfiles() {
        BancoADN_Grupo6_Pant_AdminPerfiles pantallaPerf = new BancoADN_Grupo6_Pant_AdminPerfiles();
        new BancoADN_Grupo6_Ctrl_AdminPerfiles(pantallaPerf, clienteSocket, adminLogueado);
        pantallaPerf.setVisible(true);
    }

    // ════════════════════════════════════════════════════════
    // ── SNAPSHOTS Y SOPORTE ────────────────────────────────
    // ════════════════════════════════════════════════════════

    private List<Solicitud> obtenerSnapshotSolicitudes() {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("Error de conexión con el servidor.");
            return new ArrayList<>();
        }

        List<String> lineas = clienteSocket.enviarYSolicitarLista("ListaSol");
        List<Solicitud> lista = new ArrayList<>();

        if (lineas == null) return lista;

        for (String linea : lineas) {
            String[] p = linea.split(" - ", -1);
            if (p.length < 6) continue; // Formato original 6 campos: ID(0)-Tipo(1)-Estado(2)-Datos(3)-IdPerfil(4)-Fecha(5)

            try {
                Solicitud sol = new Solicitud(
                    Integer.parseInt(p[0].trim()), // idSolicitud
                    p[1].trim(),                   // tipo
                    Integer.parseInt(p[2].trim()), // estado
                    p[3].trim(),                   // datosSolicitud
                    p[4].equals("NULL") ? -1 : Integer.parseInt(p[4].trim()), // idPerfil
                    p[5].trim()                    // fechaCreacion
                );

                if (sol.isPendiente()) {
                    lista.add(sol);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear solicitud: " + linea);
            }
        }
        return lista;
    }

    private String obtenerEmailPorPerfil(int idPerfil) {
        if (idPerfil <= 0) return "—";
        String respuesta = clienteSocket.enviarYRecibir("EmailPorPerfil - " + idPerfil);
        if (respuesta == null || respuesta.isBlank()) return "—";
        return respuesta.trim();
    }

    private void manejarCerrarSesion() {
        int opc = JOptionPane.showConfirmDialog(vista,
            "¿Deseas cerrar sesión?", "Salir", JOptionPane.YES_NO_OPTION);
        if (opc == JOptionPane.YES_OPTION) {
            vista.dispose();
            BancoADN_Grupo6_Pant_IniciarSesion loginVista = new BancoADN_Grupo6_Pant_IniciarSesion();
            new BancoADN_Grupo6_Ctrl_IniciarSesion(loginVista, clienteSocket);
            loginVista.setVisible(true);
        }
    }
}
