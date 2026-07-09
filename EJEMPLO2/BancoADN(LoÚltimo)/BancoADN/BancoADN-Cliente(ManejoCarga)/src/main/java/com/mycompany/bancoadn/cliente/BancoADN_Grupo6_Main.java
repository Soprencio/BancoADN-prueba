/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;
 
import javax.swing.SwingUtilities;
 
/**
 * Punto de entrada de la aplicación cliente SimpleADN.
 * 
 * Arranca la conexión al servidor y abre la pantalla de login.
 * @author Admin
 */
public class BancoADN_Grupo6_Main {
 
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
 
            // 1. Crear y conectar el socket (puerto 4990 del servidor)
            BancoADN_Grupo6_ClienteSocket clienteSocket = new BancoADN_Grupo6_ClienteSocket();
 
            if (!clienteSocket.estaConectado()) {
                System.err.println("ADVERTENCIA: No se pudo conectar al servidor.");
                System.err.println("Asegurate de que LadoServer esté corriendo en el puerto 4990.");
                // La app igual abre, mostrará error al intentar usar el servidor
            }
 
            // 2. Crear la pantalla de login
            BancoADN_Grupo6_Pant_IniciarSesion vistaLogin = new BancoADN_Grupo6_Pant_IniciarSesion();
 
            // 3. Crear el controlador (vincula la vista con el socket)
            BancoADN_Grupo6_Ctrl_IniciarSesion ctrl = new BancoADN_Grupo6_Ctrl_IniciarSesion(vistaLogin, clienteSocket);
 
            // 4. Mostrar la pantalla
            vistaLogin.setVisible(true);
        });
    }
}
