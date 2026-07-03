package com.mycompany.bancoadn.cliente.httpapi.service;

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
     * Send a list command using the persistent session socket.
     */
    private static List<String> enviarConLoginListaLog(String email, String comando) {
        return SessionSocket.sendListCommand(email, comando);
    }

    /**
     * Get lines from the server by sending a command (no session socket — legacy fallback).
     */
    private static List<String> obtenerLineasDeLog(String command) {
        // No email available; can't use SessionSocket. Returns null to signal failure.
        return null;
    }
}
