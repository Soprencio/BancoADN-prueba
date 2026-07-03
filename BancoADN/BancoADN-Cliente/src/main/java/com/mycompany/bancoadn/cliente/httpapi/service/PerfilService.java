package com.mycompany.bancoadn.cliente.httpapi.service;

import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;

import java.util.ArrayList;
import java.util.List;

public class PerfilService {

    /**
     * Send a single-line command using the persistent session socket.
     * Opens a new socket + login only on first use; reuses the session socket afterwards.
     */
    private static String enviarConLoginPerfil(String email, String comando) {
        return SessionSocket.sendCommand(email, comando);
    }

    /**
     * Send a list command using the persistent session socket.
     */
    private static List<String> enviarConLoginListaPerfil(String email, String comando) {
        return SessionSocket.sendListCommand(email, comando);
    }

    /**
     * Get the profile of the user with the given email.
     * Protocol: "BuscarDat - {email}"
     * If response is "No se encontro el perfil", return null.
     * Otherwise, parse line separated by " - ": index 1=nombreCompleto, index 2=codigoSecuencia,
     * index 3=descripcion, index 4=estado (int, 1=activo/0=inactivo), index 5=fechaMuestra.
     * @param email the email of the user
     * @return the profile or null if not found
     */
    public static PerfilGenetico getPerfilByEmail(String email) {
        String respuesta = enviarConLoginPerfil(email, "BuscarDat - " + email);
        if (respuesta == null || respuesta.isEmpty() || respuesta.equals("No se encontro el perfil")) {
            return null;
        }

        // Parse response: nombreCompleto - codigoSecuencia - descripcion - estado - fechaMuestra
        // According to spec: index 1=nombreCompleto, 2=codigoSecuencia, 3=descripcion, 4=estado, 5=fechaMuestra
        String[] datos = respuesta.split(" - ");
        if (datos.length >= 6) {
            return new PerfilGenetico(
                    -1, // idPerfil not available from BuscarDat
                    datos[1].trim(), // nombreCompleto
                    datos[2].trim(), // codigoSecuencia
                    datos[3].trim(), // descripcion
                    Integer.parseInt(datos[4].trim()), // estado: 1=ACTIVO, 0=INACTIVO
                    datos[5].trim(), // fechaMuestra
                    -1 // idCuenta - we don't have it here, but the caller might know it from session
            );
        }
        return null;
    }

    /**
     * Search for profiles by criteria.
     * Uses the same protocol commands as the original Swing EJEMPLO:
     *   - "ID":      BuscarIDNOM - <id>  - NULL
     *   - "Nombre":  BuscarIDNOM - NULL  - <nombre>
     *   - "Todos":   BuscarIDNOM - NULL  - NULL
     *
     * The server filters by its own criteria (equalsIgnoreCase). For partial/contains
     * matching, we additionally filter client-side on the correct field:
     *   - "ID"      → only idPerfil (string contains)
     *   - "Nombre"  → only nombreCompleto (string contains)
     *   - "Todos"   → no client-side filter (show all)
     *
     * Role filter: non-admin users only see active profiles (estado == 1).
     *
     * @param tipo       "ID", "Nombre", or "Todos"
     * @param texto      search text (case-insensitive partial match)
     * @param esAdmin    whether the caller is an admin
     * @param adminEmail admin's email for session socket
     * @return list of matching profiles
     */
    public static List<PerfilGenetico> buscarPerfiles(String tipo, String texto, boolean esAdmin, String adminEmail) {
        String comando;
        String textoT = texto.trim();

        if (("ID".equalsIgnoreCase(tipo) || "codigo".equalsIgnoreCase(tipo)) && !textoT.isEmpty()) {
            comando = "BuscarIDNOM - " + textoT + " - NULL";
        } else if ("Nombre".equalsIgnoreCase(tipo) && !textoT.isEmpty()) {
            comando = "BuscarIDNOM - NULL - " + textoT;
        } else {
            // "Todos" or empty text: fetch all profiles
            comando = "BuscarIDNOM - NULL - NULL";
        }

        List<String> lineas = enviarConLoginListaPerfil(adminEmail, comando);
        List<PerfilGenetico> results = new ArrayList<>();

        if (lineas == null) {
            return results;
        }

        for (String linea : lineas) {
            if (linea.startsWith("No se encontro")) {
                continue;
            }
            String[] p = linea.split(" - ", -1);
            if (p.length < 7) {
                continue;
            }

            try {
                int idPerfil = Integer.parseInt(p[0].trim());
                String nombreCompleto = p[1].trim();
                String codigoSecuencia = p[2].trim();
                String descripcion = p[3].trim();
                int estado = Integer.parseInt(p[4].trim());
                String fechaMuestra = p[5].trim();

                PerfilGenetico perfil = new PerfilGenetico(
                        idPerfil, nombreCompleto, codigoSecuencia, descripcion,
                        estado, fechaMuestra, -1
                );

                // Role filter: non-admin users only see active profiles
                if (!esAdmin && perfil.getEstado() != 1) {
                    continue;
                }

                // Client-side partial matching (contains, case-insensitive)
                if (("ID".equalsIgnoreCase(tipo) || "codigo".equalsIgnoreCase(tipo)) && !textoT.isEmpty()) {
                    String textLower = textoT.toLowerCase();
                    if (!String.valueOf(idPerfil).contains(textLower)) continue;
                } else if ("Nombre".equalsIgnoreCase(tipo) && !textoT.isEmpty()) {
                    String textLower = textoT.toLowerCase();
                    if (!nombreCompleto.toLowerCase().contains(textLower)) continue;
                }
                // "Todos": no additional filter, include all

                results.add(perfil);
            } catch (NumberFormatException e) {
                // Skip malformed line
            }
        }
        return results;
    }

    /**
     * Get the email of the profile owner by idPerfil (internal service method).
     * Protocol: "EmailPorPerfil - {idPerfil}"
     * @param idPerfil the ID of the profile
     * @return the email or null if not found
     */
    public static String obtenerEmailPorPerfil(int idPerfil, String callerEmail) {
        String respuesta = SessionSocket.sendCommand(callerEmail, "EmailPorPerfil - " + idPerfil);
        return (respuesta == null || respuesta.isBlank()) ? null : respuesta.trim();
    }

    /**
     * Update a profile (admin only) by sending a modification request on behalf of the profile owner.
     * Protocol: "ModificP - {emailDelTitular} - {nombre} - {codigo} - {descripcion} - {fecha}"
     * Éxito si la respuesta es exactamente "1".
     * @param idPerfil        the ID of the profile to update
     * @param nombreCompleto  the new full name
     * @param codigoSecuencia the new sequence code
     * @param descripcion     the new description
     * @param fechaMuestra    the new sample date (yyyy-MM-dd)
     * @param estado          the new status (1 for active, 0 for inactive)
     * @param adminEmail      the email of the admin performing the update (not used in request)
     * @return true if successful
     */
    public static boolean actualizarPerfil(int idPerfil, String nombreCompleto, String codigoSecuencia, String descripcion, String fechaMuestra, int estado, String adminEmail) {
        String email = obtenerEmailPorPerfil(idPerfil, adminEmail);
        if (email == null || email.equals("—")) {
            return false;
        }

        String mensaje = "ModificP - " + email + " - " + nombreCompleto + " - " + codigoSecuencia + " - " + descripcion + " - " + fechaMuestra;
        String respuesta = enviarConLoginPerfil(adminEmail, mensaje);
        return respuesta != null && respuesta.trim().equals("1");
    }

    /**
     * Deactivate a profile (admin only) by sending a baja request on behalf of the profile owner.
     * Protocol: "DarDBaja - {emailDelTitular}"
     * Respuesta éxito si contiene "true".
     * @param idPerfil   the ID of the profile to deactivate
     * @param adminEmail the email of the admin performing the action
     * @return true if successful
     */
    public static boolean deactivatePerfil(int idPerfil, String adminEmail) {
        String email = obtenerEmailPorPerfil(idPerfil, adminEmail);
        if (email == null || email.equals("—")) {
            return false;
        }

        String mensaje = "DarDBaja - " + email;
        String respuesta = enviarConLoginPerfil(adminEmail, mensaje);
        return respuesta != null && respuesta.contains("true");
    }

    /**
     * Reactivate a profile (admin only) by sending a restaurar request on behalf of the profile owner.
     * Protocol: "DarDRestaur - {emailDelTitular}"
     * Mismo criterio de éxito que DarDBaja.
     * @param idPerfil   the ID of the profile to reactivate
     * @param adminEmail the email of the admin performing the action
     * @return true if successful
     */
    public static boolean activatePerfil(int idPerfil, String adminEmail) {
        String email = obtenerEmailPorPerfil(idPerfil, adminEmail);
        if (email == null || email.equals("—")) {
            return false;
        }

        String mensaje = "DarDRestaur - " + email;
        String respuesta = enviarConLoginPerfil(adminEmail, mensaje);
        return respuesta != null && respuesta.contains("true");
    }
}