package com.mycompany.bancoadn.cliente.httpapi.service;

import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_ClienteSocket;

import java.util.List;

/**
 * Manages a persistent socket connection for an authenticated session.
 * Opens one socket, sends IniciarS once, and reuses it for all subsequent
 * commands. This avoids duplicate "Iniciar sesión" and "Conectarse al servidor"
 * logs that occur when opening a fresh socket + login per command.
 */
public class SessionSocket {

    private static BancoADN_Grupo6_ClienteSocket sessionSocket = null;
    private static String sessionEmail = null;

    /**
     * Get the persistent session socket if still connected.
     * The socket must have been previously set via setSocket() after a successful login.
     * @param email the expected session email (for validation)
     * @return the connected socket, or null if no active session
     */
    public static synchronized BancoADN_Grupo6_ClienteSocket getSocket(String email) {
        if (sessionSocket != null && sessionSocket.estaConectado() && email.equals(sessionEmail)) {
            return sessionSocket;
        }
        return null;
    }

    /**
     * Store an authenticated socket for reuse across bridge requests.
     */
    public static synchronized void setSocket(BancoADN_Grupo6_ClienteSocket socket, String email) {
        if (sessionSocket != null && sessionSocket != socket) {
            sessionSocket.desconectar();
        }
        sessionSocket = socket;
        sessionEmail = email;
    }

    /**
     * Send a single-command on the session socket and return the single-line response.
     * @param email email for (re)authentication if socket needs reopening
     * @param command the command to send
     * @return the response line, or null on failure
     */
    public static synchronized String sendCommand(String email, String command) {
        BancoADN_Grupo6_ClienteSocket socket = getSocket(email);
        if (socket == null) {
            return null;
        }
        return socket.enviarYRecibir(command);
    }

    /**
     * Send a list-command on the session socket and return all lines until FINISH.
     * @param email email for (re)authentication if socket needs reopening
     * @param command the command to send
     * @return the list of response lines, or null on failure
     */
    public static synchronized List<String> sendListCommand(String email, String command) {
        BancoADN_Grupo6_ClienteSocket socket = getSocket(email);
        if (socket == null) {
            return null;
        }
        return socket.enviarYSolicitarLista(command);
    }

    /**
     * Close the persistent session socket.
     */
    public static synchronized void close() {
        if (sessionSocket != null) {
            sessionSocket.desconectar();
            sessionSocket = null;
        }
        sessionEmail = null;
    }

    /**
     * Check whether a session socket is currently open.
     */
    public static synchronized boolean isActive() {
        return sessionSocket != null && sessionSocket.estaConectado();
    }
}