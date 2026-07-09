package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

/**
 * Controlador para la pantalla Solicitar Perfil.
 *
 * CAPA DE MODELO:
 *   Recibe CuentaPersonal como credencial de sesión.
 *   Usa cuentaActual.getEmail() para armar el mensaje al servidor.
 *
 * Responsabilidad:
 *   1. Tomar los datos del formulario
 *   2. Validarlos localmente
 *   3. Enviar solicitud al servidor
 *   4. Mostrar respuesta al usuario
 */
public class BancoADN_Grupo6_Ctrl_SolicitarPerfil implements ActionListener {

    private BancoADN_Grupo6_Pant_SolicitarPerfil vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
    private CuentaPersonal cuentaActual;

    public BancoADN_Grupo6_Ctrl_SolicitarPerfil(BancoADN_Grupo6_Pant_SolicitarPerfil vista,
                                                  BancoADN_Grupo6_ClienteSocket clienteSocket,
                                                  CuentaPersonal cuentaActual) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.cuentaActual = cuentaActual;

        this.vista.agregarListenerEnviarSolicitud(this);
        this.vista.agregarListenerVolver(e -> volverAlMenu());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String nombrePerfil    = vista.getNombrePerfil();
        String codigoSecuencia = vista.getCodigoSecuencia();
        String descripcion     = vista.getDescripcion();
        String fechaMuestra    = vista.getFechaMuestra();

        // --- VALIDACIONES LOCALES ---
        if (nombrePerfil.isEmpty()) {
            vista.mostrarError("El nombre del perfil es obligatorio.");
            return;
        }

        if (codigoSecuencia.isEmpty()) {
            vista.mostrarError("El código de secuencia es obligatorio.");
            return;
        }

        if (descripcion.isEmpty()) {
            vista.mostrarError("La descripción es obligatoria.");
            return;
        }

        if (fechaMuestra.isEmpty()) {
            vista.mostrarError("La fecha de muestra es obligatoria (formato: yyyy-MM-dd).");
            return;
        }

        if (nombrePerfil.length() < 3) {
            vista.mostrarError("El nombre del perfil debe tener al menos 3 caracteres.");
            return;
        }

        if (codigoSecuencia.length() < 3) {
            vista.mostrarError("El código de secuencia debe tener al menos 3 caracteres.");
            return;
        }

        if (!validarFormatoFecha(fechaMuestra)) {
            vista.mostrarError("La fecha debe estar en formato yyyy-MM-dd (Ej: 2026-04-23).");
            return;
        }

        solicitarRegistrarPerfil(nombrePerfil, codigoSecuencia, descripcion, fechaMuestra);
    }

    /**
     * Ensambla el mensaje en texto plano y lo envía al servidor.
     * Formato: "CrearPerfilSol - email - email _ nombre _ codigo _ descripcion _ fecha"
     *
     * El email se obtiene de la credencial de sesión, nunca de un campo de texto.
     */
    private void solicitarRegistrarPerfil(String nombre, String codigo, String descripcion, String fecha) {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("No hay conexión con el servidor.\nVerificá que el servidor esté activo.");
            return;
        }

        String email   = cuentaActual.getEmail();
        String mensaje = "CrearPerfilSol - " + email + " - " + email + " _ " + nombre + " _ "
                       + codigo + " _ " + descripcion + " _ " + fecha;

        vista.mostrarCargando(true);
 
        new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                return clienteSocket.enviarYRecibir(mensaje);
            }
 
            @Override
            protected void done() {
                try {
                    String respuesta = get();
                    procesarRespuesta(respuesta);
                } catch (Exception ex) {
                    vista.mostrarError("Error al comunicarse con el servidor.");
                } finally {
                    vista.mostrarCargando(false);
                }
            }
        }.execute();
    }

    private void procesarRespuesta(String respuesta) {
        if (respuesta == null) {
            vista.mostrarError("No se recibió respuesta del servidor.\nIntentá de nuevo.");
            return;
        }

        if (respuesta.toLowerCase().contains("exito")) {
            vista.mostrarMensaje("¡Solicitud enviada exitosamente!\nEl administrador revisará tu solicitud.");
            vista.limpiarCampos();
            volverAlMenu();
        } else {
            vista.mostrarError("El servidor respondió:\n" + respuesta);
        }
    }

    private void volverAlMenu() {
        vista.dispose();
        BancoADN_Grupo6_MenuUsuario menuVista = new BancoADN_Grupo6_MenuUsuario();
        menuVista.setEmailUsuario(cuentaActual.getEmail());
        menuVista.setNombreUsuario(cuentaActual.getNombreCuenta());
        BancoADN_Grupo6_Ctrl_MenuUsuario ctrlMenuUsuario = new BancoADN_Grupo6_Ctrl_MenuUsuario(
            menuVista, clienteSocket, cuentaActual
        );
        menuVista.setVisible(true);
    }

    private boolean validarFormatoFecha(String fecha) {
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}