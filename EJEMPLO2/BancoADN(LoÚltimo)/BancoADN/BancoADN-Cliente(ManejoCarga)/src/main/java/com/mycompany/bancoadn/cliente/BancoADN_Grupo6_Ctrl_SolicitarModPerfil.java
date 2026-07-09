/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;
/**
 * Controlador para solicitar modificación de un perfil genético.
 *
 * CAPA DE MODELO:
 *   Recibe CuentaPersonal como credencial de sesión.
 *   Usa cuentaActual.getEmail() para armar el mensaje al servidor.
 *
 *   Solicitud → "CrearSolPer - email - email _ nombre _ codigo _ descripcion _ fecha - modificar"
 *
 *   IMPORTANTE:
 *   - El separador principal es " - " (espacio-guión-espacio)
 *   - El separador interno de datos es " _ "
 *   - El tipo de solicitud (modificar) va al final
 */
public class BancoADN_Grupo6_Ctrl_SolicitarModPerfil implements ActionListener {

    private BancoADN_Grupo6_Pant_SolicitarModPerfil vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
    private CuentaPersonal cuentaActual;

    public BancoADN_Grupo6_Ctrl_SolicitarModPerfil(BancoADN_Grupo6_Pant_SolicitarModPerfil vista,
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

        solicitarModificarPerfil(nombrePerfil, codigoSecuencia, descripcion, fechaMuestra);
    }

    /**
     * Ensambla el mensaje en texto plano y lo envía al servidor.
     * El email se obtiene de la credencial de sesión, nunca de un campo de texto.
     *
     * Parseado así por el servidor:
     *   Partes[0] = "CrearSolPer"
     *   Partes[1] = email
     *   Partes[2] = "email _ nombre _ codigo _ descripcion _ fecha"
     *   Partes[3] = "modificar"
     */
    private void solicitarModificarPerfil(String nombre, String codigo, String descripcion, String fecha) {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("No hay conexión con el servidor.\nVerificá que el servidor esté activo.");
            return;
        }

        String email            = cuentaActual.getEmail();
        String datosModificacion = email + " _ " + nombre + " _ " + codigo + " _ " + descripcion + " _ " + fecha;
        String mensaje           = "CrearSolPer - " + email + " - " + datosModificacion + " - modificar";

        System.out.println("Enviando modificación: " + mensaje); // Debug

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

        System.out.println("Respuesta del servidor: " + respuesta); // Debug

        if (respuesta.toLowerCase().contains("exito")) {
            vista.mostrarMensaje("¡Solicitud de modificación enviada exitosamente!\n"
                    + "El administrador revisará tu solicitud.");
            vista.limpiarCampos();
            volverAlMenu();
        } else {
            vista.mostrarError("El servidor respondió:\n" + respuesta);
        }
    }

    private void volverAlMenu() {
        vista.dispose();
        BancoADN_Grupo6_MenuUsuario menu = new BancoADN_Grupo6_MenuUsuario();
        menu.setEmailUsuario(cuentaActual.getEmail());
        menu.setNombreUsuario(cuentaActual.getNombreCuenta());
        BancoADN_Grupo6_Ctrl_MenuUsuario ctrlMenu = new BancoADN_Grupo6_Ctrl_MenuUsuario(
            menu, clienteSocket, cuentaActual
        );
        menu.setVisible(true);
    }

    private boolean validarFormatoFecha(String fecha) {
        return fecha.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
