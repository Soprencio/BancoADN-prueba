/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;
 
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaCrearCuenta;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
public class BancoADN_Grupo6_Ctrl_CrearCuenta implements ActionListener {
 
    private IVistaCrearCuenta vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
 
    public BancoADN_Grupo6_Ctrl_CrearCuenta(IVistaCrearCuenta vista, BancoADN_Grupo6_ClienteSocket clienteSocket) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
    }
 
    public void ejecutarCrearCuenta(String nombreCuenta, String email, String contraseña) {
        if (nombreCuenta == null) nombreCuenta = "";
        if (email == null) email = "";
        if (contraseña == null) contraseña = "";
        nombreCuenta = nombreCuenta.trim();
        email = email.trim();
 
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
 
    @Override
    public void actionPerformed(ActionEvent e) {
        ejecutarCrearCuenta(vista.getNombreCuenta(), vista.getEmail(), vista.getContraseña());
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
 
    public void volverAlLogin() {
        vista.dispose();
        vista.navegarALogin();
    }
 
    private boolean validarEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
