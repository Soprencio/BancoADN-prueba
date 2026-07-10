package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaModificarPerfilAdmin;

public class BancoADN_Grupo6_Ctrl_ModificarPerfilAdmin {

    private final IVistaModificarPerfilAdmin vista;
    private final BancoADN_Grupo6_ClienteSocket clienteSocket;
    private final String emailTitular;
    private final CuentaPersonal adminLogueado;

    public BancoADN_Grupo6_Ctrl_ModificarPerfilAdmin(IVistaModificarPerfilAdmin vista,
                                     BancoADN_Grupo6_ClienteSocket clienteSocket,
                                     String emailTitular,
                                     CuentaPersonal adminLogueado) {
        this.vista         = vista;
        this.clienteSocket = clienteSocket;
        this.emailTitular  = emailTitular;
        this.adminLogueado = adminLogueado;
    }

    public void ejecutarModificacion(String nombre, String codigo, String desc, String fecha) {
        if (nombre == null) nombre = "";
        if (codigo == null) codigo = "";
        if (desc == null) desc = "";
        if (fecha == null) fecha = "";

        if (nombre.isEmpty()) { vista.mostrarError("El nombre del perfil es obligatorio."); return; }
        if (nombre.length() < 3) { vista.mostrarError("El nombre debe tener al menos 3 caracteres."); return; }
        if (codigo.isEmpty()) { vista.mostrarError("El código de secuencia es obligatorio."); return; }
        if (codigo.length() < 3) { vista.mostrarError("El código debe tener al menos 3 caracteres."); return; }
        if (desc.isEmpty()) { vista.mostrarError("La descripción es obligatoria."); return; }
        if (fecha.isEmpty()) { vista.mostrarError("La fecha es obligatoria (formato: yyyy-MM-dd)."); return; }
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) { vista.mostrarError("La fecha debe estar en formato yyyy-MM-dd (Ej: 2026-04-23)."); return; }

        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("No hay conexión con el servidor.");
            return;
        }

        String mensaje = "ModificP - " + emailTitular + " - "
                       + nombre + " - " + codigo + " - " + desc + " - " + fecha;

        String respuesta = clienteSocket.enviarYRecibir(mensaje);

        if (respuesta == null) {
            vista.mostrarError("Sin respuesta del servidor. Intentá de nuevo.");
            return;
        }

        if (respuesta.trim().equals("1")) {
            vista.mostrarMensaje("¡Perfil modificado correctamente!");
            vista.dispose();
        } else {
            vista.mostrarError("El servidor no pudo modificar el perfil.");
        }
    }
}
