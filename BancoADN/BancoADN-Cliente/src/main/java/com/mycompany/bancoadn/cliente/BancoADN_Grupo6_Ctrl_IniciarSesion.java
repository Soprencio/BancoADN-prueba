/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.Rol;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaIniciarSesion;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class BancoADN_Grupo6_Ctrl_IniciarSesion implements ActionListener {

    private IVistaIniciarSesion vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;

    public BancoADN_Grupo6_Ctrl_IniciarSesion(IVistaIniciarSesion vista, BancoADN_Grupo6_ClienteSocket clienteSocket) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
    }

    public void ejecutarLogin(String email, String contraseña) {
        if (email == null || contraseña == null) {
            vista.mostrarError("Por favor completa todos los campos.");
            return;
        }
        email = email.trim();
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

    @Override
    public void actionPerformed(ActionEvent e) {
        ejecutarLogin(vista.getEmail(), vista.getContraseña());
    }

    /*
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
        String respuesta = clienteSocket.enviarYRecibir(mensaje);

        procesarRespuesta(respuesta, email);
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
            case 1: {
                String nombre = email.split("@")[0];
                CuentaPersonal usuarioLogueado = new CuentaPersonal(-1, nombre, "", email, 1);
                Rol rol = new Rol(1, "USUARIO");
                vista.limpiarCampos();
                vista.dispose();
                vista.navegarAMenuUsuario(email, nombre, rol.getIdRol());
                break;
            }
            case 2: {
                String nombre = email.split("@")[0];
                CuentaPersonal adminLogueado = new CuentaPersonal(-1, nombre, "", email, 2);
                Rol rol = new Rol(2, "ADMIN");
                vista.limpiarCampos();
                vista.dispose();
                vista.navegarAMenuAdmin(email, nombre, rol.getIdRol());
                break;
            }
            case -1:
                vista.mostrarError("Email o contraseña incorrectos.");
                vista.limpiarCampos();
                break;
        }
    }

    public void abrirRegistro() {
        vista.dispose();
        vista.navegarARegistro();
    }

    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}