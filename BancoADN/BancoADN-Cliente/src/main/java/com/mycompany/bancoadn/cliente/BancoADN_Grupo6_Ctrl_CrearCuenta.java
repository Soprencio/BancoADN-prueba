/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;
 
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
/**
 *Controlador de la pantalla Crear Cuenta.
 *
 *Protocolo con el servidor
 *
 *Solicitud → "CrearC - email - nombreCuenta - contraseña"
 *
 *ORDEN: email primero, luego nombre, luego contraseña
 *Ejemplo real: "CrearC - pepe@gmail.com - Pepe - 1234"
 *
 *Respuesta servidor:
 *"Creado completado con exito"  → cuenta creada OK
 *"Ya existe una cuenta con ese email" → email ya registrado */
public class BancoADN_Grupo6_Ctrl_CrearCuenta implements ActionListener {
 
    private BancoADN_Grupo6_Pant_CrearCuenta vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
 
    public BancoADN_Grupo6_Ctrl_CrearCuenta(BancoADN_Grupo6_Pant_CrearCuenta vista, BancoADN_Grupo6_ClienteSocket clienteSocket) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
 
        this.vista.agregarListenerBotonIngresar(this);
        this.vista.agregarListenerVolver(e -> volverAlLogin());
    }
 
    @Override
    public void actionPerformed(ActionEvent e) {
        String nombreCuenta = vista.getNombreCuenta().trim();
        String email        = vista.getEmail().trim();
        String contraseña   = vista.getContraseña();
 
        // --- VALIDACIONES LOCALES ---
        if (nombreCuenta.isEmpty() || email.isEmpty() || contraseña.isEmpty()) {
            vista.mostrarError("Por favor completa todos los campos.");
            return;
        }
 
        if (!validarEmail(email)) {
            vista.mostrarError("El formato del email es inválido.\nEjemplo válido: usuario@mail.com");
            return;
        }
 
        if (contraseña.length() < 4) {
            vista.mostrarError("La contraseña debe tener al menos 4 caracteres.");
            return;
        }
 
        if (nombreCuenta.length() < 3) {
            vista.mostrarError("El nombre debe tener al menos 3 caracteres.");
            return;
        }
 
        crearCuenta(nombreCuenta, email, contraseña);
    }
 
    /**
     * Envía la solicitud al servidor.
     *
     *RequestHandler parsea:
     *   Partes[0] = "CrearC"
     *   Partes[1] = email       → setString(1, Partes[1])
     *   Partes[2] = nombre      → setString(2, Partes[2])
     *   Partes[3] = contraseña  → setString(3, Partes[3])
     */
    private void crearCuenta(String nombreCuenta, String email, String contraseña) {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("No hay conexión con el servidor.\nVerificá que el servidor esté activo.");
            return;
        }
 
        String mensaje = "CrearC - " + email + " - " + nombreCuenta + " - " + contraseña;
 
        String respuesta = clienteSocket.enviarYRecibir(mensaje);
 
        procesarRespuesta(respuesta);
    }
 
    /*Interpreta la respuesta del servidor.
     * Servidor responde "Creado completado con exito" o "Ya existe una cuenta con ese email"*/
    private void procesarRespuesta(String respuesta) {
        if (respuesta == null) {
            vista.mostrarError("No se recibió respuesta del servidor.\nIntentá de nuevo.");
            return;
        }
 
        if (respuesta.equalsIgnoreCase("Creado completado con exito")) {
            vista.mostrarMensaje("¡Cuenta creada exitosamente!\nYa podés iniciar sesión.");
            vista.limpiarCampos();
            volverAlLogin();
        } else if (respuesta.equalsIgnoreCase("Ya existe una cuenta con ese email")) {
            vista.mostrarError("Ya existe una cuenta registrada con ese email.\nProbá con otro.");
        } else {
            vista.mostrarError("Respuesta inesperada del servidor:\n" + respuesta);
        }
    }
 
    private void volverAlLogin() {
        vista.dispose();
        BancoADN_Grupo6_Pant_IniciarSesion vistaLogin = new BancoADN_Grupo6_Pant_IniciarSesion();
        BancoADN_Grupo6_Ctrl_IniciarSesion ctrlLogin = new BancoADN_Grupo6_Ctrl_IniciarSesion(vistaLogin, clienteSocket);
        vistaLogin.setVisible(true);
    }
 
    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
