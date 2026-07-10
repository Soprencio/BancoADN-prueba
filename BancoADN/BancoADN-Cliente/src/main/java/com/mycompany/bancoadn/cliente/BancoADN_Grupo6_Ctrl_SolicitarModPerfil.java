package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaSolicitarModPerfil;

public class BancoADN_Grupo6_Ctrl_SolicitarModPerfil {

    private IVistaSolicitarModPerfil vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
    private CuentaPersonal cuentaActual;

    public BancoADN_Grupo6_Ctrl_SolicitarModPerfil(IVistaSolicitarModPerfil vista,
                                                    BancoADN_Grupo6_ClienteSocket clienteSocket,
                                                    CuentaPersonal cuentaActual) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.cuentaActual = cuentaActual;
    }

    public void ejecutarSolicitud(String nombre, String codigo, String descripcion, String fecha) {
        if (nombre == null) nombre = "";
        if (codigo == null) codigo = "";
        if (descripcion == null) descripcion = "";
        if (fecha == null) fecha = "";

        if (nombre.isEmpty()) { vista.mostrarError("El nombre del perfil es obligatorio."); return; }
        if (codigo.isEmpty()) { vista.mostrarError("El código de secuencia es obligatorio."); return; }
        if (descripcion.isEmpty()) { vista.mostrarError("La descripción es obligatoria."); return; }
        if (fecha.isEmpty()) { vista.mostrarError("La fecha de muestra es obligatoria (formato: yyyy-MM-dd)."); return; }
        if (nombre.length() < 3) { vista.mostrarError("El nombre del perfil debe tener al menos 3 caracteres."); return; }
        if (codigo.length() < 3) { vista.mostrarError("El código de secuencia debe tener al menos 3 caracteres."); return; }
        if (!validarFormatoFecha(fecha)) { vista.mostrarError("La fecha debe estar en formato yyyy-MM-dd (Ej: 2026-04-23)."); return; }

        solicitarModificarPerfil(nombre, codigo, descripcion, fecha);
    }

    private void solicitarModificarPerfil(String nombre, String codigo, String descripcion, String fecha) {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("No hay conexión con el servidor.\nVerificá que el servidor esté activo.");
            return;
        }

        String email            = cuentaActual.getEmail();
        String datosModificacion = email + " _ " + nombre + " _ " + codigo + " _ " + descripcion + " _ " + fecha;
        String mensaje           = "CrearSolPer - " + email + " - " + datosModificacion + " - modificar";

        String respuesta = clienteSocket.enviarYRecibir(mensaje);
        procesarRespuesta(respuesta);
    }

    private void procesarRespuesta(String respuesta) {
        if (respuesta == null) {
            vista.mostrarError("No se recibió respuesta del servidor.\nIntentá de nuevo.");
            return;
        }

        if (respuesta.toLowerCase().contains("exito")) {
            vista.mostrarMensaje("¡Solicitud de modificación enviada exitosamente!\n"
                    + "El administrador revisará tu solicitud.");
            vista.limpiarCampos();
            vista.dispose();
            vista.navegarAMenu();
        } else {
            vista.mostrarError("El servidor respondió:\n" + respuesta);
        }
    }

    public void manejarVolver() {
        vista.dispose();
        vista.navegarAMenu();
    }

    private boolean validarFormatoFecha(String fecha) {
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
