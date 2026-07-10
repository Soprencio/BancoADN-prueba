package com.mycompany.bancoadn.cliente;

import java.util.List;
import java.util.ArrayList;
import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.Solicitud;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaMenuAdmin;


public class BancoADN_Grupo6_Ctrl_MenuAdmin {

    private final IVistaMenuAdmin vista;
    private final BancoADN_Grupo6_ClienteSocket clienteSocket;
    private final CuentaPersonal adminLogueado;

    public BancoADN_Grupo6_Ctrl_MenuAdmin(IVistaMenuAdmin vista, 
                                           BancoADN_Grupo6_ClienteSocket clienteSocket, 
                                           CuentaPersonal adminLogueado) {
        this.vista         = vista;
        this.clienteSocket = clienteSocket;
        this.adminLogueado = adminLogueado;
    }

    public void consultarSolicitudesPendientes() {
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

            vista.agregarTarjetaSolicitud(
                sol.getIdSolicitud(),
                mailMostrar,
                sol.getFechaCreacion(),
                sol.getTipo(),
                lineaExtra,
                sol.getIdSolicitud(),
                sol.getDatosSolicitud()
            );
        }
    }

    public void resolverSolicitud(int idSolicitud, int estadoNuevo) {
        if (!verificarEstadoSolicitud(idSolicitud)) {
            vista.mostrarError("La solicitud #" + idSolicitud + " ya ha sido procesada por otro administrador.");
            consultarSolicitudesPendientes();
            return;
        }

        String pregunta = (estadoNuevo == 1)
            ? "¿Confirmás la APROBACIÓN de la solicitud #" + idSolicitud + "?"
            : "¿Confirmás el RECHAZO de la solicitud #"   + idSolicitud + "?";

        if (!vista.confirmar(pregunta)) return;

        String mensaje = "ResSol - " + idSolicitud + " - " + estadoNuevo + " - " + adminLogueado.getEmail();
        String respuesta = clienteSocket.enviarYRecibir(mensaje);
        procesarResolucion(idSolicitud, respuesta, estadoNuevo);
    }

    private boolean verificarEstadoSolicitud(int idSolicitud) {
        List<Solicitud> pendientes = obtenerSnapshotSolicitudes();
        for (Solicitud s : pendientes) {
            if (s.getIdSolicitud() == idSolicitud && s.isPendiente()) {
                return true;
            }
        }
        return false;
    }

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
            if (p.length < 6) continue;

            try {
                Solicitud sol = new Solicitud(
                    Integer.parseInt(p[0].trim()),
                    p[1].trim(),
                    Integer.parseInt(p[2].trim()),
                    p[3].trim(),
                    p[4].equals("NULL") ? -1 : Integer.parseInt(p[4].trim()),
                    p[5].trim()
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

    public void manejarCerrarSesion() {
        if (!vista.confirmar("¿Deseas cerrar sesión?")) return;
        vista.dispose();
        vista.navegarALogin();
    }

    public CuentaPersonal getAdminLogueado() { return adminLogueado; }
    public BancoADN_Grupo6_ClienteSocket getClienteSocket() { return clienteSocket; }
}
