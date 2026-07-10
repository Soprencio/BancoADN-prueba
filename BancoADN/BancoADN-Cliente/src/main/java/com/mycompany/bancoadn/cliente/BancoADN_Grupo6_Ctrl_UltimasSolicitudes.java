package com.mycompany.bancoadn.cliente;

import java.util.List;
import java.util.ArrayList;
import com.mycompany.bancoadn.cliente.ClasesModelo.Solicitud;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaUltimasSolicitudes;

public class BancoADN_Grupo6_Ctrl_UltimasSolicitudes {

    private final IVistaUltimasSolicitudes vista;
    private final BancoADN_Grupo6_ClienteSocket clienteSocket;

    public BancoADN_Grupo6_Ctrl_UltimasSolicitudes(IVistaUltimasSolicitudes vista, BancoADN_Grupo6_ClienteSocket clienteSocket) {
        this.vista         = vista;
        this.clienteSocket = clienteSocket;
        cargarUltimasSolicitudes();
    }

    private List<Solicitud> obtenerSnapshotUltimasSolicitudes() {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("Error de conexión con el servidor.");
            return new ArrayList<>();
        }

        List<String> lineas = clienteSocket.enviarYSolicitarLista("UltSol");
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
                
                if (!sol.isPendiente()) {
                    lista.add(sol);
                }
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear última solicitud: " + linea);
            }
        }
        return lista;
    }

    private void cargarUltimasSolicitudes() {
        vista.limpiarSolicitudes();
        List<Solicitud> solicitudes = obtenerSnapshotUltimasSolicitudes();

        if (solicitudes.isEmpty()) {
            vista.mostrarSinSolicitudes();
            return;
        }

        for (Solicitud sol : solicitudes) {
            String fechaResolucion = "—"; 

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

            String estadoTexto = sol.isAprobada() ? "✓ Aceptada" : "✗ Rechazada";

            vista.agregarTarjetaSolicitud(
                sol.getIdSolicitud(),
                mailMostrar,
                sol.getFechaCreacion(),
                fechaResolucion,
                sol.getTipo(),
                estadoTexto,
                lineaExtra,
                sol.getDatosSolicitud()
            );
        }
    }

    private String obtenerEmailPorPerfil(int idPerfil) {
        if (idPerfil <= 0) return "—";
        String respuesta = clienteSocket.enviarYRecibir("EmailPorPerfil - " + idPerfil);
        return (respuesta == null || respuesta.isBlank()) ? "—" : respuesta.trim();
    }
}
