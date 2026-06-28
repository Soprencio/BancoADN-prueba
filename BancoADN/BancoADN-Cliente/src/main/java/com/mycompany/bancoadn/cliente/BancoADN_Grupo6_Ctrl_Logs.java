package com.mycompany.bancoadn.cliente;

import java.util.List;

public class BancoADN_Grupo6_Ctrl_Logs {

    private final BancoADN_Grupo6_Pant_Logs vista;
    private final BancoADN_Grupo6_ClienteSocket clienteSocket;
    private final String idCuenta; // "ALL" para admin, o el ID numérico para usuario

    public BancoADN_Grupo6_Ctrl_Logs(BancoADN_Grupo6_Pant_Logs vista, BancoADN_Grupo6_ClienteSocket clienteSocket, String idCuenta) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.idCuenta = idCuenta;

        vista.agregarListenerCerrar(e -> vista.dispose());
        cargarLogs();
    }

    private void cargarLogs() {
        vista.limpiarLogs();

        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("No hay conexión con el servidor.");
            return;
        }

        List<String> lineas = clienteSocket.enviarYSolicitarLista("GetLogs - " + idCuenta);

        if (lineas == null) {
            vista.mostrarError("Error al conectar con el servidor.");
            return;
        }

        if (lineas.isEmpty()) {
            vista.mostrarSinLogs();
            return;
        }

        int count = 0;
        // iterate reverse to get newest first
        for (int i = lineas.size() - 1; i >= 0 && count < 30; i--) {
            String linea = lineas.get(i);
            String[] p = linea.split(" - ", -1);
            if (p.length < 8) {
                System.err.println("Línea malformada (GetLogs), ignorada: " + linea);
                continue;
            }
            try {
                int idLog = Integer.parseInt(p[0].trim());
                String nombreCuenta = p[2].trim();
                String email = p[3].trim();
                String descripcion = p[4].trim();
                String fecha = p[5].trim();
                String acciones = p[6].trim();
                boolean isAdminLog = p[7].trim().equals("1");
                vista.agregarTarjetaLog(idLog, nombreCuenta, email, descripcion, fecha, acciones, isAdminLog);
                count++;
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear datos de Log: " + p[0]);
            }
        }
    }
}
