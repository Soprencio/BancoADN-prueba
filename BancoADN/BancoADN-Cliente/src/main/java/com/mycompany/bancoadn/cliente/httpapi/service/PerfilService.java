package com.mycompany.bancoadn.cliente.httpapi.service;

import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_ClienteSocket;
import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;

import java.util.ArrayList;
import java.util.List;

public class PerfilService {

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
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return null;
        }

        String mensaje = "BuscarDat - " + email;
        String respuesta = socket.enviarYRecibir(mensaje);
        socket.desconectar();

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
     * Protocol: "BuscarIDNOM - {id o NULL} - {nombre o NULL}" (poné NULL literal en el campo que no se usa)
     * Usá clienteSocket.enviarYSolicitarLista(), que ya devuelve la lista de líneas sin el "FINISH" final.
     * Cada línea tiene 7 campos separados por " - ": idPerfil, nombreCompleto, codigoSecuencia, descripcion, estado, fechaMuestra, email.
     * Para buscar "todos", enviá NULL en ambos campos. Filtrá los resultados a solo estado=activo si quien busca tiene rol Usuario;
     * si es Administrador, devolvé todos sin filtrar.
     * @param tipo   "ID" or "Nombre"
     * @param texto  the value to search for
     * @return list of matching profiles
     */
    public static List<PerfilGenetico> buscarPerfiles(String tipo, String texto, boolean esAdmin) {
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return new ArrayList<>();
        }

        String comando;
        if (tipo.equalsIgnoreCase("ID") && !texto.isEmpty()) {
            comando = "BuscarIDNOM - " + texto + " - NULL";
        } else if (tipo.equalsIgnoreCase("Nombre") && !texto.isEmpty()) {
            comando = "BuscarIDNOM - NULL - " + texto;
        } else {
            // "Todos" or empty
            comando = "BuscarIDNOM - NULL - NULL";
        }

        List<String> lineas = socket.enviarYSolicitarLista(comando);
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
                PerfilGenetico perfil = new PerfilGenetico(
                        Integer.parseInt(p[0].trim()), // idPerfil
                        p[1].trim(),                   // nombreCompleto
                        p[2].trim(),                   // codigoSecuencia
                        p[3].trim(),                   // descripcion
                        Integer.parseInt(p[4].trim()), // estado
                        p[5].trim(),                   // fechaMuestra
                        -1 // idCuenta - we don't have it here, but the caller might know it from session
                );
                // Filter by role: if user is not admin, only active (estado == 1)
                if (!esAdmin && perfil.getEstado() != 1) {
                    continue;
                }
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
    public static String obtenerEmailPorPerfil(int idPerfil) {
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return null;
        }
        String respuesta = socket.enviarYRecibir("EmailPorPerfil - " + idPerfil);
        socket.desconectar();
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
        String email = obtenerEmailPorPerfil(idPerfil);
        if (email == null || email.equals("—")) {
            return false;
        }
        // Use the direct modification protocol (not via solicitud)
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return false;
        }

        String mensaje = "ModificP - " + email + " - " + nombreCompleto + " - " + codigoSecuencia + " - " + descripcion + " - " + fechaMuestra;
        String respuesta = socket.enviarYRecibir(mensaje);
        socket.desconectar();

        return respuesta != null && respuesta.trim().equals("1");
    }

    /**
     * Deactivate a profile (admin only) by sending a baja request on behalf of the profile owner.
     * Protocol: "DarDBaja - {emailDelTitular}"
     * Respuesta éxito si contiene "true".
     * @param idPerfil   the ID of the profile to deactivate
     * @param adminEmailertificating the action
     * @return true if successful
     */
    public static boolean deactivatePerfil(int idPerfil, String adminEmail) {
        String email = obtenerEmailPorPerfil(idPerfil);
        if (email == null || email.equals("—")) {
            return false;
        }
        // Use the direct deactivation protocol (not via solicitud)
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return false;
        }

        String mensaje = "DarDBaja - " + email;
        String respuesta = socket.enviarYRecibir(mensaje);
        socket.desconectar();

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
        String email = obtenerEmailPorPerfil(idPerfil);
        if (email == null || email.equals("—")) {
            return false;
        }
        // Use the direct reactivation protocol (not via solicitud)
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return false;
        }

        String mensaje = "DarDRestaur - " + email;
        String respuesta = socket.enviarYRecibir(mensaje);
        socket.desconectar();

        return respuesta != null && respuesta.contains("true");
    }
}