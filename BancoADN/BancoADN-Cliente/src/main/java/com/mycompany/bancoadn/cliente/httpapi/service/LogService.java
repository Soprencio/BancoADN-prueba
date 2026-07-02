package com.mycompany.bancoadn.cliente.httpapi.service;

import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_ClienteSocket;
import com.mycompany.bancoadn.cliente.httpapi.dto.LogDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Service for log operations.
 */
public class LogService {

    /**
     * Get all logs (uses a cached/session socket — left untouched for compatibility).
     * @return list of log DTOs
     */
    public static List<LogDto> getLogs() {
        return getLogs(null);
    }

    /**
     * Get all logs.
     * Protocol: "GetLogs - ALL"
     * Each line has 8 fields: idLog, unusedField, nombreCuenta, email, descripcion, fecha, acciones, esAdmin
     * Keep only the last 30 most recent lines.
     * Requires re-login with adminEmail because LadoServer's ObtenerLogsAdmin calls
     * obtenerNombre() which needs mailusr ThreadLocal set.
     * @param adminEmail the admin's email for re-login (may be null to skip login)
     * @return list of log DTOs
     */
    public static List<LogDto> getLogs(String adminEmail) {
        List<String> lines;
        if (adminEmail != null) {
            lines = enviarConLoginListaLog(adminEmail, "GetLogs - ALL");
        } else {
            lines = obtenerLineasDeLog("GetLogs - ALL");
        }

        List<LogDto> logs = new ArrayList<>();

        if (lines == null) {
            return logs;
        }

        for (String line : lines) {
            LogDto log = LogDto.fromString(line);
            if (log != null) {
                logs.add(log);
            }
        }

        // Sort by idLog descending (most recent first) and take only the last 30.
        // Matches original Swing behavior: last 30 lines in the file (idLog increments sequentially).
        Collections.sort(logs, (log1, log2) -> Integer.compare(log2.getIdRegistro(), log1.getIdRegistro()));
        if (logs.size() > 30) {
            return logs.subList(0, 30);
        }
        return logs;
    }

    /**
     * Open a fresh socket, re-login, send a list command, read until FINISH, and close.
     * Same pattern as PerfilService.enviarConLoginListaPerfil.
     */
    private static List<String> enviarConLoginListaLog(String email, String comando) {
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
        List<String> respuestas = socket.enviarYSolicitarLista(comando);
        socket.desconectar();
        return respuestas;
    }

    /**
     * Get lines from the server by sending a command (no re-login — legacy).
     * @param command the command to send (e.g., "GetLogs - ALL")
     * @return list of response lines, or null if failed
     */
    private static List<String> obtenerLineasDeLog(String command) {
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return null;
        }

        // Send the command and get a list of strings.
        List<String> lineas = socket.enviarYSolicitarLista(command);
        socket.desconectar();
        return lineas;
    }
}
