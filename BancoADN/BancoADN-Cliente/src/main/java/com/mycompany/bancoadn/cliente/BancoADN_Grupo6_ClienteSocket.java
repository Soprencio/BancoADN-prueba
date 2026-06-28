/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;
 
import java.io.*;
import java.net.Socket;
 
/**
 * Clase que maneja la conexión por socket con el servidor.
 * 
 * PROTOCOLO DEL SERVIDOR:
 *   - Puerto: 4990
 *   - Separador: " - " (espacio guion espacio)
 *   - Ejemplo: "CrearC - nombreCuenta - email - contraseña"
 * 
 * @author Admin
 */
public class BancoADN_Grupo6_ClienteSocket {
 
    private static final String HOST = "localhost";
    private static final int PUERTO = 4990; 
 
    private Socket socket;
    private PrintWriter escritor;
    private BufferedReader lector;
    private boolean conectado = false;
 
    /**
     * Constructor: intenta conectar al servidor automáticamente.
     */
    public BancoADN_Grupo6_ClienteSocket() {
        conectar();
    }
 
    /**
     * Establece la conexión con el servidor.
     * @return true si se conectó, false si hubo error
     */
    public boolean conectar() {
        try {
            socket = new Socket(HOST, PUERTO);
            escritor = new PrintWriter(socket.getOutputStream(), true);
            lector = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            conectado = true;
            System.out.println("✓ Conectado al servidor " + HOST + ":" + PUERTO);
            return true;
        } catch (IOException e) {
            conectado = false;
            System.err.println("✗ No se pudo conectar: " + e.getMessage());
            return false;
        }
    }
 
    public String enviarYRecibir(String mensaje) {
        try {
            if (!conectado) {
                System.err.println("No conectado al servidor");
                return null;
            }
            System.out.println("→ Enviando: " + mensaje);
            escritor.println(mensaje);
            String respuesta = lector.readLine();
            System.out.println("← Recibido: " + respuesta);
            return respuesta;
        } catch (IOException e) {
            conectado = false;
            System.err.println("Error en comunicación: " + e.getMessage());
            return null;
        }
    }
 
    /**
     * Cierra la conexión con el servidor.
     */
    public void desconectar() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                conectado = false;
                System.out.println("✓ Desconectado");
            }
        } catch (IOException e) {
            System.err.println("Error al desconectar: " + e.getMessage());
        }
    }
 
    public boolean estaConectado() {
        return conectado && socket != null && !socket.isClosed();
    }
    
    public java.util.List<String> enviarYSolicitarLista(String mensaje) {
        try {
            if (!conectado) {
                System.err.println("No conectado al servidor");
                return null;
            }
            System.out.println("→ Enviando (Lista): " + mensaje);
            escritor.println(mensaje);

            java.util.List<String> respuestas = new java.util.ArrayList<>();
            String linea;
            
            // Leemos líneas hasta encontrar "FINISH"
            while ((linea = lector.readLine()) != null) {
                if (linea.equals("FINISH")) {
                    System.out.println("← Recepción de lista completada.");
                    break;
                }
                System.out.println("← Recibido (ítem): " + linea);
                respuestas.add(linea);
            }
            return respuestas;

        } catch (IOException e) {
            conectado = false;
            System.err.println("Error en comunicación: " + e.getMessage());
            return null;
        }
    }
}
