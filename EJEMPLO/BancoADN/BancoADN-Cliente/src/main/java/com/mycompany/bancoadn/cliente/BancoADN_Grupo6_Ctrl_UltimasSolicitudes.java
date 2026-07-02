package com.mycompany.bancoadn.cliente;

import java.util.List;
import java.util.ArrayList;
import com.mycompany.bancoadn.cliente.ClasesModelo.Solicitud;

public class BancoADN_Grupo6_Ctrl_UltimasSolicitudes {

    private final BancoADN_Grupo6_Pant_UltimasSolicitudes vista;
    private final BancoADN_Grupo6_ClienteSocket           clienteSocket;

    public BancoADN_Grupo6_Ctrl_UltimasSolicitudes(BancoADN_Grupo6_Pant_UltimasSolicitudes vista, BancoADN_Grupo6_ClienteSocket clienteSocket) {
        this.vista         = vista;
        this.clienteSocket = clienteSocket;

        vista.agregarListenerCerrar(e -> vista.dispose());
        cargarUltimasSolicitudes();
    }

    // ════════════════════════════════════════════════════════
    // ── SNAPSHOT DE ÚLTIMAS SOLICITUDES ────────────────────
    // ════════════════════════════════════════════════════════

    /**
     * Consulta el servidor y devuelve una lista de objetos Solicitud frescos.
     * Solo devuelve solicitudes ya resueltas (estado 1 o 2).
     */
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
            if (p.length < 6) continue; // Formato original 6 campos: id(0), tipo(1), estado(2), datos(3), idPerfil(4), fecha(5)

            try {
                Solicitud sol = new Solicitud(
                    Integer.parseInt(p[0].trim()), // idSolicitud
                    p[1].trim(),                   // tipo
                    Integer.parseInt(p[2].trim()), // estado
                    p[3].trim(),                   // datosSolicitud
                    p[4].equals("NULL") ? -1 : Integer.parseInt(p[4].trim()), // idPerfil
                    p[5].trim()                    // fechaCreacion
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
            // Como usamos el formato original de 6 campos, la fecha de resolución no está disponible
            // Se mostrará un guion o la misma fecha de creación si se prefiere.
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
            java.awt.Color estadoColor = sol.isAprobada()
                ? vista.getColorAceptada()
                : vista.getColorRechazada();

            vista.agregarTarjetaSolicitud(
                sol.getIdSolicitud(),
                mailMostrar,
                sol.getFechaCreacion(),
                fechaResolucion,
                sol.getTipo(),
                estadoTexto,
                estadoColor,
                lineaExtra
            );
        }
    }

    private String obtenerEmailPorPerfil(int idPerfil) {
        if (idPerfil <= 0) return "—";
        String respuesta = clienteSocket.enviarYRecibir("EmailPorPerfil - " + idPerfil);
        return (respuesta == null || respuesta.isBlank()) ? "—" : respuesta.trim();
    }
}
