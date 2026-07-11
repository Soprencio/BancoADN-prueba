package com.mycompany.bancoadn.cliente.httpapi.service;

import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_ClienteSocket;

import java.util.List;

/** Gestiona una conexion socket persistente para la sesion autenticada */
public class SessionSocket {

    private static BancoADN_Grupo6_ClienteSocket sessionSocket = null;
    private static String sessionEmail = null;

    /**
     * Obtiene el socket persistente de la sesion si sigue conectado.
     * @param email email de la sesion esperada (para validacion)
     * @return el socket conectado, o null si no hay sesion activa
     */
    public static synchronized BancoADN_Grupo6_ClienteSocket getSocket(String email) {
        if (sessionSocket != null && sessionSocket.estaConectado() && email.equals(sessionEmail)) {
            return sessionSocket;
        }
        return null;
    }

    /**
     * Guarda un socket autenticado para reutilizarlo entre requests del bridge.
     */
    public static synchronized void setSocket(BancoADN_Grupo6_ClienteSocket socket, String email) {
        if (sessionSocket != null && sessionSocket != socket) {
            sessionSocket.desconectar();
        }
        sessionSocket = socket;
        sessionEmail = email;
    }

    /**
     * Envia un comando individual en el socket de sesion y devuelve la respuesta.
     * @param email email para (re)autenticacion si el socket necesita reconexion
     * @param command el comando a enviar
     * @return la linea de respuesta, o null si falla
     */
    public static synchronized String sendCommand(String email, String command) {
        BancoADN_Grupo6_ClienteSocket socket = getSocket(email);
        if (socket == null) {
            return null;
        }
        return socket.enviarYRecibir(command);
    }

    /**
     * Envia un comando de lista en el socket de sesion y devuelve todas las lineas hasta FINISH.
     * @param email email para (re)autenticacion si el socket necesita reconexion
     * @param command el comando a enviar
     * @return la lista de lineas de respuesta, o null si falla
     */
    public static synchronized List<String> sendListCommand(String email, String command) {
        BancoADN_Grupo6_ClienteSocket socket = getSocket(email);
        if (socket == null) {
            return null;
        }
        return socket.enviarYSolicitarLista(command);
    }

    /**
     * Cierra el socket persistente de la sesion.
     */
    public static synchronized void close() {
        if (sessionSocket != null) {
            sessionSocket.desconectar();
            sessionSocket = null;
        }
        sessionEmail = null;
    }

    /**
     * Verifica si el socket de sesion esta actualmente abierto.
     */
    public static synchronized boolean isActive() {
        return sessionSocket != null && sessionSocket.estaConectado();
    }
}