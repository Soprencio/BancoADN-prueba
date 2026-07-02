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
     * Get all logs.
     * Protocol: "GetLogs - ALL"
     * Each line has 8 fields: idLog, unusedField, nombreCuenta, email, descripcion, fecha, acciones, esAdmin
     * Keep only the last 30 most recent lines.
     * @return list of log DTOs
     */
    public static List<LogDto> getLogs() {
        // We'll use the socket to send a command to get logs.
        List<String> lines = obtenerLineasDeLog("GetLogs - ALL");
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

        // Sort by date descending (most recent first) and take only the last 30
        Collections.sort(logs, (log1, log2) -> log2.getFecha().compareTo(log1.getFecha()));
        if (logs.size() > 30) {
            return logs.subList(0, 30);
        }
        return logs;
    }

    /**
     * Get lines from the server by sending a command.
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
