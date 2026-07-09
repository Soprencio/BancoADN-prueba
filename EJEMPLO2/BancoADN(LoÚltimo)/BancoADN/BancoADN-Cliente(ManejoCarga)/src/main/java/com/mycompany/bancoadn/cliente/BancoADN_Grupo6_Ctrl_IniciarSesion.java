/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal; //referencias clase de la subcarpeta ClasesModelo a utilizar
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingWorker;

/**
 * Controlador de la pantalla Iniciar Sesión.
 *
 * Protocolo con el servidor:
 *   Solicitud → "IniciarS - email - contraseña"
 *   Respuesta → "0", "1", o "-1"
 *     1 = USUARIO NORMAL
 *     2 = ADMINISTRADOR
 *    -1 = NO ENCONTRADO
 *
 * Con la capa de modelo, la respuesta del servidor se convierte
 * en un objeto CuentaPersonal antes de pasarlo a las siguientes pantallas.
 */
public class BancoADN_Grupo6_Ctrl_IniciarSesion implements ActionListener {

    private BancoADN_Grupo6_Pant_IniciarSesion vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;

    public BancoADN_Grupo6_Ctrl_IniciarSesion(BancoADN_Grupo6_Pant_IniciarSesion vista, BancoADN_Grupo6_ClienteSocket clienteSocket) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;

        this.vista.agregarListenerBotonIniciarSesion(this);
        this.vista.agregarListenerRegistro(e -> abrirRegistro());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String email      = vista.getEmail().trim();
        String contraseña = vista.getContraseña();

        if (email.isEmpty() || contraseña.isEmpty()) {
            vista.mostrarError("Por favor completa todos los campos.");
            return;
        }

        if (!validarEmail(email)) {
            vista.mostrarError("El formato del email es inválido.\nEjemplo: usuario@mail.com");
            return;
        }

        iniciarSesion(email, contraseña);
    }

    /* Envía la solicitud al servidor en un hilo separado.
     *
     * Mientras espera:
     *   - El botón se deshabilita (evita doble envío)
     *   - El cursor cambia a reloj de espera
     *
     * Al recibir respuesta:
     *   - El botón se rehabilita
     *   - El cursor vuelve al normal
     *
     * Envía la solicitud al servidor.
     * RequestHandler parsea:
     *   Partes[0] = "IniciarS"
     *   Partes[1] = email
     *   Partes[2] = contraseña
     */
    private void iniciarSesion(String email, String contraseña) {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("No hay conexión con el servidor.\nVerificá que el servidor esté activo.");
            return;
        }

        String mensaje   = "IniciarS - " + email + " - " + contraseña;
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
                    procesarRespuesta(respuesta, email);
                } catch (Exception ex) {
                    vista.mostrarError("Error al comunicarse con el servidor.");
                } finally {
                    vista.mostrarCargando(false);
                }
            }
        }.execute();
    }

    /**
     * El servidor responde con un código numérico.
     * Se construye un objeto CuentaPersonal con los datos disponibles
     * en el momento del login y se lo pasa a la siguiente pantalla.
     *
     * Nota: el servidor no devuelve idCuenta ni nombre en el login,
     * por eso idCuenta se inicializa en -1 y el nombre se deriva del email.
     * La contraseña y el idCuenta no se guardan en el objeto por seguridad.
     */
    private void procesarRespuesta(String respuesta, String email) {
        if (respuesta == null) {
            vista.mostrarError("No se recibió respuesta del servidor.\nIntentá de nuevo.");
            return;
        }

        int codigo = Integer.parseInt(respuesta.trim());

        switch (codigo) {
            case 1: // USUARIO NORMAL
                CuentaPersonal usuarioLogueado = new CuentaPersonal(
                    -1,                   // idCuenta: no disponible en login
                    email.split("@")[0],  // nombreCuenta: derivado del email
                    "",                   // contraseña: no se guarda por seguridad
                    email,                // email
                    1                     // idRol: 1 = USUARIO
                );

                vista.limpiarCampos();
                vista.dispose();
                BancoADN_Grupo6_MenuUsuario menu = new BancoADN_Grupo6_MenuUsuario();
                menu.setEmailUsuario(usuarioLogueado.getEmail());
                menu.setNombreUsuario(usuarioLogueado.getNombreCuenta());
                BancoADN_Grupo6_Ctrl_MenuUsuario ctrlMenuUsuario = new BancoADN_Grupo6_Ctrl_MenuUsuario(menu, clienteSocket, usuarioLogueado);
                menu.setVisible(true);
                break;
                
            case 2: // ADMINISTRADOR
                CuentaPersonal adminLogueado = new CuentaPersonal(
                    -1,                   // idCuenta: no disponible en login
                    email.split("@")[0],  // nombreCuenta: derivado del email
                    "",                   // contraseña: no se guarda por seguridad
                    email,                // email
                    2                     // idRol: 2 = ADMIN
                );

                vista.limpiarCampos();
                vista.dispose();
                BancoADN_Grupo6_MenuAdmin menuAdmin = new BancoADN_Grupo6_MenuAdmin();
                menuAdmin.setEmailAdmin(adminLogueado.getEmail());
                menuAdmin.setNombreAdmin(adminLogueado.getNombreCuenta());
                BancoADN_Grupo6_Ctrl_MenuAdmin ctrlMenuAdmin = new BancoADN_Grupo6_Ctrl_MenuAdmin(menuAdmin, clienteSocket, adminLogueado);
                menuAdmin.setVisible(true);
                break;

            case -1: // NO ENCONTRADO O CONTRASEÑA MAL
                vista.mostrarError("Email o contraseña incorrectos.");
                vista.limpiarCampos();
                break;
        }
    }

    private void abrirRegistro() {
        vista.dispose();
        BancoADN_Grupo6_Pant_CrearCuenta vistaRegistro = new BancoADN_Grupo6_Pant_CrearCuenta();
        BancoADN_Grupo6_Ctrl_CrearCuenta ctrlRegistro  = new BancoADN_Grupo6_Ctrl_CrearCuenta(vistaRegistro, clienteSocket);
        vistaRegistro.setVisible(true);
    }

    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}