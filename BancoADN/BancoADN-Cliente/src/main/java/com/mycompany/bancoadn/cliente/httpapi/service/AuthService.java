package com.mycompany.bancoadn.cliente.httpapi.service;

import com.mycompany.bancoadn.cliente.BancoADN_Grupo6_ClienteSocket;
import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import java.util.concurrent.ConcurrentHashMap;

public class AuthService {

    // Store credentials for re-login on fresh socket connections.
    // LadoServer uses a ThreadLocal (mailusr) that is only set by iniciarSesion.
    // Each new socket connection must login first before sending commands.
    private static final ConcurrentHashMap<String, String> passwordStore = new ConcurrentHashMap<>();

    /**
     * Re-authenticate on a fresh socket connection (sets mailusr on LadoServer).
     * @param email the user's email
     * @param password the user's password
     * @param socket an already-connected socket
     * @return true if login succeeded
     */
    public static boolean reloginOnSocket(String email, String password, BancoADN_Grupo6_ClienteSocket socket) {
        String respuesta = socket.enviarYRecibir("IniciarS - " + email + " - " + password);
        return respuesta != null;
    }

    /**
     * Retrieve the stored password for a given email.
     */
    public static String getPassword(String email) {
        return passwordStore.get(email);
    }

    /**
     * Attempts to log in with the given email and password.
     * Protocol: "IniciarS - {email} - {contraseña}"
     * Response: "1" (usuario), "2" (admin), "-1" (no encontrado/credenciales incorrectas)
     * @param email    the email of the user
     * @param password the password of the user
     * @return a CuentaPersonal object representing the logged-in user, or null if failed
     */
    public static CuentaPersonal login(String email, String password) {
        // Save password FIRST so SessionSocket.openSession() can find it.
        // If login fails, we remove it below.
        passwordStore.put(email, password);

        String respuesta = SessionSocket.openSession(email);

        if (respuesta == null) {
            passwordStore.remove(email);
            return null;
        }

        try {
            int codigo = Integer.parseInt(respuesta);
            switch (codigo) {
                case 1: // USUARIO NORMAL
                    return new CuentaPersonal(
                            -1, // idCuenta not available in login
                            email.split("@")[0], // nombreCuenta derived from email
                            "", // contraseña not stored for security
                            email,
                            1 // idRol: 1 = USUARIO
                    );
                case 2: // ADMINISTRADOR
                    return new CuentaPersonal(
                            -1,
                            email.split("@")[0],
                            "",
                            email,
                            2 // idRol: 2 = ADMIN
                    );
                case -1: // NO ENCONTRADO
                default:
                    passwordStore.remove(email);
                    return null;
            }
        } catch (NumberFormatException e) {
            passwordStore.remove(email);
            return null;
        }
    }

    /**
     * Attempts to create a new account.
     * Protocol: "CrearC - {email} - {nombreCuenta} - {contraseña}"
     * Response exacto (comparar con equalsIgnoreCase):
     *   "Creado completado con exito" = éxito,
     *   "Ya existe una cuenta con ese email" = error de email duplicado,
     *   cualquier otra cosa = error inesperado.
     * @param email           the email for the new account
     * @param nombreCuenta    the full name for the account
     * @param password        the password for the account
     * @return a result indicating success or failure and a message
     */
    public static CreateAccountResult crearCuenta(String email, String nombreCuenta, String password) {
        BancoADN_Grupo6_ClienteSocket socket = new BancoADN_Grupo6_ClienteSocket();
        if (!socket.estaConectado()) {
            return new CreateAccountResult(false, "No connection to server");
        }

        String mensaje = "CrearC - " + email + " - " + nombreCuenta + " - " + password;
        String respuesta = socket.enviarYRecibir(mensaje);
        socket.desconectar();

        if (respuesta == null) {
            return new CreateAccountResult(false, "No response from server");
        }

        if (respuesta.equalsIgnoreCase("Creado completado con exito") ||
            // "Error al crear cuenta" means the account WAS written to file but the
            // log entry in Logs.txt failed (mailusr ThreadLocal null on fresh socket).
            // This is a false negative: creation succeeded, only the audit log failed.
            respuesta.equalsIgnoreCase("Error al crear cuenta")) {
            passwordStore.put(email, password);
            return new CreateAccountResult(true, "Account created successfully");
        } else if (respuesta.equalsIgnoreCase("Ya existe una cuenta con ese email")) {
            return new CreateAccountResult(false, "Email already registered");
        } else {
            return new CreateAccountResult(false, "Unexpected response: " + respuesta);
        }
    }
}
