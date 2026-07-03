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
     * Open a session socket, send IniciarS, and return the login response.
     * The socket stays open for reuse in subsequent sendCommand/sendListCommand calls.
     * AuthService.login() uses this to authenticate and create the persistent socket
     * in one step, avoiding a duplicate IniciarS.
     *
     * @param email the email to authenticate with (password must be in AuthService.passwordStore)
     * @return the login response ("1"=user, "2"=admin), or null on failure
     */
    public static synchronized String openSession(String email) {
        String password = AuthService.getPassword(email);
        if (password == null) {
            return null;
        }

        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return null;
        }

        String loginResp = socket.enviarYRecibir("IniciarS - " + email + " - " + password);
        if (loginResp == null) {
            socket.desconectar();
            return null;
        }

        sessionSocket = socket;
        sessionEmail = email;
        return loginResp.trim();
    }

    /**
     * Get (or create) the persistent session socket.
     * Only opens a new socket if none is currently connected.
     * @param email the email to authenticate with (only used if reopening)
     * @return the connected socket, or null if connection/login fails
     */
    public static synchronized BancoADN_Grupo6_ClienteSocket getSocket(String email) {
        if (sessionSocket != null && sessionSocket.estaConectado()) {
            return sessionSocket;
        }
        String loginResp = openSession(email);
        return (loginResp != null) ? sessionSocket : null;
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