package com.mycompany.bancoadn.cliente.httpapi.service;

import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_ClienteSocket;
import com.mycompany.bancoadn.cliente.ClasesModelo.Solicitud;

import java.util.ArrayList;
import java.util.List;

/**
 * Service layer for solicitud operations, communicating directly with the socket server
 * using the exact protocol defined in the requirements.
 * 
 * IMPORTANT: LadoServer uses a ThreadLocal (mailusr) set only by the iniciarSesion command.
 * Each fresh socket connection MUST send "IniciarS" before any other command to set mailusr,
 * otherwise methods like obtenerNombre() and obtenerIdCuenta(mailusr.get()) will throw NPE.
 * This mirrors the Swing client behavior which keeps one persistent authenticated connection.
 */
public class SolicitudService {

    /**
     * Open a socket, re-login (to set mailusr on LadoServer), send the command,
     * read response, and close.
     */
    private static String enviarConLogin(String email, String comando) {
        String password = AuthService.getPassword(email);
        if (password == null) {
            System.err.println("No stored password for " + email + " - cannot re-login");
            return null;
        }

        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return null;
        }

        // Step 1: login to set mailusr ThreadLocal on LadoServer
        String loginResp = socket.enviarYRecibir("IniciarS - " + email + " - " + password);
        if (loginResp == null) {
            socket.desconectar();
            return null;
        }

        // Step 2: send the actual command
        String respuesta = socket.enviarYRecibir(comando);
        socket.desconectar();
        return respuesta;
    }

    /**
     * Open a socket, re-login, send a list command, read multi-line response until FINISH, and close.
     */
    private static List<String> enviarConLoginLista(String email, String comando) {
        String password = AuthService.getPassword(email);
        if (password == null) {
            System.err.println("No stored password for " + email + " - cannot re-login");
            return null;
        }

        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return null;
        }

        // Step 1: login to set mailusr
        String loginResp = socket.enviarYRecibir("IniciarS - " + email + " - " + password);
        if (loginResp == null) {
            socket.desconectar();
            return null;
        }

        // Step 2: send the list command
        List<String> respuestas = socket.enviarYSolicitarLista(comando);
        socket.desconectar();
        return respuestas;
    }

    /**
     * Submit a new registration request.
     * Protocol: "CrearPerfilSol - {email} - {email} _ {nombrePerfil} _ {codigoSecuencia} _ {descripcion} _ {fechaMuestra}"
     * Success if response contains "exito" (case-insensitive).
     */
    public static boolean solicitarRegistrar(String email, String nombrePerfil, String codigoSecuencia, String descripcion, String fechaMuestra) {
        String mensaje = "CrearPerfilSol - " + email + " - "
                + email + " _ " + nombrePerfil + " _ " + codigoSecuencia + " _ " + descripcion + " _ " + fechaMuestra;
        String respuesta = enviarConLogin(email, mensaje);
        return respuesta != null && respuesta.toLowerCase().contains("exito");
    }

    /**
     * Submit a modification request.
     * Protocol: "CrearSolPer - {email} - {email} _ {nombrePerfil} _ {codigoSecuencia} _ {descripcion} _ {fechaMuestra} - modificar"
     * Success if response contains "exito" (case-insensitive).
     */
    public static boolean solicitarModificar(String email, String nombrePerfil, String codigoSecuencia, String descripcion, String fechaMuestra) {
        String mensaje = "CrearSolPer - " + email + " - "
                + email + " _ " + nombrePerfil + " _ " + codigoSecuencia + " _ " + descripcion + " _ " + fechaMuestra + " - modificar";
        String respuesta = enviarConLogin(email, mensaje);
        return respuesta != null && respuesta.toLowerCase().contains("exito");
    }

    /**
     * Submit a deactivation request.
     * Protocol: "CrearSolPer - {email} -   - baja"
     * Success if response contains "exito" (case-insensitive).
     */
    public static boolean solicitarBaja(String email) {
        String respuesta = enviarConLogin(email, "CrearSolPer - " + email + " -   - baja");
        return respuesta != null && respuesta.toLowerCase().contains("exito");
    }

    /**
     * Submit a reactivation request.
     * Protocol: "CrearSolPer - {email} -   - restaurar"
     * Success if response contains "exito" (case-insensitive).
     */
    public static boolean solicitarRestaurar(String email) {
        String respuesta = enviarConLogin(email, "CrearSolPer - " + email + " -   - restaurar");
        return respuesta != null && respuesta.toLowerCase().contains("exito");
    }

    /**
     * Get pending requests for a user.
     * Protocol: send "ListaSol", use enviarYSolicitarLista().
     * Each line has 6 fields: idSolicitud, tipo, estado (0/1/2), datosSolicitud (raw), idPerfil (or -1 if "NULL"), fechaCreacion.
     * Filter locally for estado == 0 (pendiente).
     */
    public static List<Solicitud> getPendingRequests(String email) {
        List<String> lineas = enviarConLoginLista(email, "ListaSol");
        List<Solicitud> result = new ArrayList<>();

        if (lineas == null) {
            return result;
        }

        for (String linea : lineas) {
            if (linea.startsWith("No se encontro")) {
                continue;
            }
            String[] parts = linea.split(" - ", -1);
            if (parts.length < 6) {
                continue;
            }

            try {
                int idSolicitud = Integer.parseInt(parts[0].trim());
                String tipo = parts[1].trim();
                int estado = Integer.parseInt(parts[2].trim());
                // Only pending requests
                if (estado != 0) {
                    continue;
                }
                String datosSolicitud = parts[3].trim(); // keep raw, do not parse further
                int idPerfil;
                String idPerfilStr = parts[4].trim();
                if ("NULL".equalsIgnoreCase(idPerfilStr)) {
                    idPerfil = -1;
                } else {
                    idPerfil = Integer.parseInt(idPerfilStr);
                }
                String fechaCreacion = parts[5].trim();

                Solicitud sol = new Solicitud(
                        idSolicitud,
                        tipo,
                        estado,
                        datosSolicitud,
                        idPerfil,
                        fechaCreacion
                );
                result.add(sol);
            } catch (NumberFormatException e) {
                // skip malformed line
            }
        }
        return result;
    }

    /**
     * Get recent resolved requests for a user.
     * Protocol: send "UltSol", same format as ListaSol.
     * Filter locally for estado != 0 (i.e., 1=approved, 2=rejected).
     */
    public static List<Solicitud> getRecentRequests(String email) {
        List<String> lineas = enviarConLoginLista(email, "UltSol");
        List<Solicitud> result = new ArrayList<>();

        if (lineas == null) {
            return result;
        }

        for (String linea : lineas) {
            if (linea.startsWith("No se encontro")) {
                continue;
            }
            String[] parts = linea.split(" - ", -1);
            if (parts.length < 6) {
                continue;
            }

            try {
                int idSolicitud = Integer.parseInt(parts[0].trim());
                String tipo = parts[1].trim();
                int estado = Integer.parseInt(parts[2].trim());
                // Only resolved requests (approved or rejected)
                if (estado == 0) {
                    continue;
                }
                String datosSolicitud = parts[3].trim(); // raw
                int idPerfil;
                String idPerfilStr = parts[4].trim();
                if ("NULL".equalsIgnoreCase(idPerfilStr)) {
                    idPerfil = -1;
                } else {
                    idPerfil = Integer.parseInt(idPerfilStr);
                }
                String fechaCreacion = parts[5].trim();

                Solicitud sol = new Solicitud(
                        idSolicitud,
                        tipo,
                        estado,
                        datosSolicitud,
                        idPerfil,
                        fechaCreacion
                );
                result.add(sol);
            } catch (NumberFormatException e) {
                // skip malformed line
            }
        }
        return result;
    }

    /**
     * Get email associated with a genetic profile, by profile id.
     * Protocol: "EmailPorPerfil - {idPerfil}"
     * Replicates Ctrl_MenuAdmin.obtenerEmailPorPerfil() from the old Swing client.
     */
    public static String obtenerEmailPorPerfil(String callerEmail, int idPerfil) {
        String respuesta = enviarConLogin(callerEmail, "EmailPorPerfil - " + idPerfil);
        if (respuesta == null || respuesta.trim().equals("—")) {
            return null;
        }
        return respuesta.trim();
    }

    /**
     * Approve a pending request (admin only).
     * Protocol: "ResSol - {idSolicitud} - 1 - {adminEmail}"
     * Success if response trimmed is "1" or "0".
     */
    public static boolean approveRequest(int idSolicitud, String adminEmail) {
        String mensaje = "ResSol - " + idSolicitud + " - 1 - " + adminEmail;
        String respuesta = enviarConLogin(adminEmail, mensaje);
        if (respuesta == null) {
            return false;
        }
        String trimmed = respuesta.trim();
        return trimmed.equals("1") || trimmed.equals("0");
    }

    /**
     * Reject a pending request (admin only).
     * Protocol: "ResSol - {idSolicitud} - 2 - {adminEmail}"
     * Success if response trimmed is "1" or "0".
     */
    public static boolean rejectRequest(int idSolicitud, String adminEmail) {
        String mensaje = "ResSol - " + idSolicitud + " - 2 - " + adminEmail;
        String respuesta = enviarConLogin(adminEmail, mensaje);
        if (respuesta == null) {
            return false;
        }
        String trimmed = respuesta.trim();
        return trimmed.equals("1") || trimmed.equals("0");
    }
}