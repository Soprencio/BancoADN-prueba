package com.mycompany.bancoadn.cliente;

import java.util.List;
import com.mycompany.bancoadn.cliente.ClasesModelo.Registro;
import com.mycompany.bancoadn.cliente.ClasesModelo.TipoAccion;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaLogs;

public class BancoADN_Grupo6_Ctrl_Logs {

    private final IVistaLogs vista;
    private final BancoADN_Grupo6_ClienteSocket clienteSocket;
    private final String idCuenta;

    public BancoADN_Grupo6_Ctrl_Logs(IVistaLogs vista, BancoADN_Grupo6_ClienteSocket clienteSocket, String idCuenta) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.idCuenta = idCuenta;
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
        for (int i = lineas.size() - 1; i >= 0 && count < 30; i--) {
            String linea = lineas.get(i);
            String[] p = linea.split(" - ", -1);
            if (p.length < 8) {
                System.err.println("Línea malformada (GetLogs), ignorada: " + linea);
                continue;
            }
            try {
                int idReg = Integer.parseInt(p[0].trim());
                String nombreCuenta = p[2].trim();
                String email = p[3].trim();
                String descripcion = p[4].trim();
                String fecha = p[5].trim();
                String acciones = p[6].trim();
                boolean esAdmin = p[7].trim().equals("1");

                // Usar clases modelo Registro y TipoAccion
                Registro registro = new Registro(idReg, 0, 0, fecha, descripcion);
                TipoAccion tipoAccion = new TipoAccion(0, acciones);

                vista.agregarTarjetaLog(
                    registro.getIdRegistro(), nombreCuenta, email,
                    registro.getDetalles(), registro.getFechaRegistro(),
                    tipoAccion.getNombreAccion(), esAdmin
                );
                count++;
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear datos de Log: " + p[0]);
            }
        }
    }
}
