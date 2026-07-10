/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaMenuUsuario;

/**
 * Controlador del menú de usuario.
 *
 * CAPA DE MODELO:
 *   - Recibe CuentaPersonal como credencial de sesión (viene del login, vive toda la sesión)
 *   - Crea objetos PerfilGenetico como snapshots desechables al consultar el servidor
 *
 * Reglas de negocio:
 *   - Solicitar perfil:      Solo si perfil == null (SIN_PERFIL)
 *   - Solicitar modificar:   Solo si perfil.isActivo()
 *   - Solicitar desactivar:  Solo si perfil.isActivo()
 *   - Solicitar reactivar:   Solo si !perfil.isActivo() y perfil != null
 *
 * @author Admin
 */
public class BancoADN_Grupo6_Ctrl_MenuUsuario {

    private IVistaMenuUsuario vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
    private CuentaPersonal cuentaActual; // Credencial de sesión

    public BancoADN_Grupo6_Ctrl_MenuUsuario(IVistaMenuUsuario vista,
                                             BancoADN_Grupo6_ClienteSocket clienteSocket,
                                             CuentaPersonal cuentaActual) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.cuentaActual = cuentaActual;
    }

    // ════════════════════════════════════════════════════════
    // ── SNAPSHOT DE PERFIL ─────────────────────────────────
    // ════════════════════════════════════════════════════════

    /**
     * Consulta el servidor y devuelve un objeto PerfilGenetico fresco.
     *
     * Este objeto es un snapshot desechable — nace, cumple su función
     * (configurar botones o mostrar datos) y no se almacena en la clase.
     *
     * Formato respuesta BuscarDat:
     *   nombreCompleto - códigoSecuencia - descripción - fechaMuestra - estado
     *   Estado: 1=ACTIVO, 0=INACTIVO
     *
     * Retorna:
     *   PerfilGenetico → si el usuario tiene perfil
     *   null           → si no tiene perfil o hubo error
     */
    private PerfilGenetico obtenerSnapshotPerfil() {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("Error de conexión con el servidor.");
            return null;
        }

        String comando  = "BuscarDat - " + cuentaActual.getEmail();
        String respuesta = clienteSocket.enviarYRecibir(comando);

        if (respuesta == null || respuesta.isEmpty()) {
            return null;
        }

        if (respuesta.equals("No se encontro el perfil")) {
            return null; // null representa SIN_PERFIL
        }

        // Parsear respuesta a objeto PerfilGenetico
        // Formato: nombreCompleto(0) - códigoSecuencia(1) - descripción(2) - fechaMuestra(3) - estado(4)
        String[] datos = respuesta.split(" - ");
        if (datos.length >= 5) {
            return new PerfilGenetico(
                -1,             // idPerfil: no disponible en BuscarDat
                datos[1].trim(), // nombreCompleto
                datos[2].trim(), // codigoSecuencia
                datos[3].trim(), // descripcion
                Integer.parseInt(datos[4].trim()), // estado: 1=ACTIVO, 0=INACTIVO
                datos[5].trim(), // fechaMuestra
                cuentaActual.getIdCuenta() // idCuenta de la sesión activa
            );
        }

        return null;
    }

    // ════════════════════════════════════════════════════════
    // ── MANEJO DE BOTONES ──────────────────────────────────
    // ════════════════════════════════════════════════════════

    public void manejarBuscar() {
        vista.dispose();
        vista.navegarABuscar();
    }

    // ── VER PERFIL ACTUAL ──────────────────────────────────
    /**
     * Obtiene un snapshot del perfil y lo muestra en el panel derecho.
     * El snapshot muere al finalizar este método.
     */
    public void manejarVerPerfil() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();

        if (perfil == null) {
            vista.mostrarMensaje("No tienes un perfil registrado.\nUsa el botón 'Solicitar Perfil' de la izquierda.");
            vista.limpiarPerfil();
            return;
        }

        // Usamos getters en lugar de índices de array
        vista.mostrarPerfil(
            perfil.getNombreCompleto(),
            perfil.getCodigoSecuencia(),
            perfil.getDescripcion(),
            perfil.isActivo() ? "ACTIVO" : "INACTIVO",
            perfil.getFechaMuestra()
        );
    }

    public String validarSolicitarPerfil() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();
        if (perfil != null) {
            return "Ya tienes un perfil vinculado a tu cuenta. No puedes solicitar un nuevo perfil.";
        }
        return null;
    }

    public void manejarSolicitarPerfil() {
        String error = validarSolicitarPerfil();
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        vista.dispose();
        vista.navegarASolicitarPerfil();
    }

    public String validarSolicitarModificacion() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();
        if (perfil == null) {
            return "No tienes un perfil registrado. Primero debes solicitar un perfil para poder modificarlo.";
        }
        if (!perfil.isActivo()) {
            return "Tu perfil está desactivado. Debes restaurarlo antes de poder modificarlo.";
        }
        return null;
    }

    public void manejarSolicitarModificacion() {
        String error = validarSolicitarModificacion();
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        vista.dispose();
        vista.navegarASolicitarModificacion();
    }

    // ── SOLICITAR DESACTIVAR ───────────────────────────────
    /**
     * REGLA DE NEGOCIO: Solo si perfil existe Y está ACTIVO
     */
    public String validarSolicitarDesactivar() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();
        if (perfil == null) {
            return "No tienes un perfil registrado.\nNo hay nada que desactivar.";
        }
        if (!perfil.isActivo()) {
            return "Tu perfil ya está desactivado.\nSi deseas reactivarlo, usa 'Solicitar Reactivar Perfil'.";
        }
        return null;
    }

    public void manejarSolicitarDesactivar() {
        String error = validarSolicitarDesactivar();
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        if (!vista.confirmar("¿Estás seguro de que deseas solicitar la desactivación de tu perfil?")) {
            return;
        }
        solicitarDesactivarPerfil();
    }

    private void solicitarDesactivarPerfil() {
        String mensaje   = "CrearSolPer - " + cuentaActual.getEmail() + " -   - baja";
        String respuesta = clienteSocket.enviarYRecibir(mensaje);
        procesarRespuestaSolicitud(respuesta, "desactivación");
    }

    public String validarSolicitarReactivar() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();
        if (perfil == null) {
            return "No tienes un perfil registrado.\nUsa 'Solicitar Perfil' para crear uno.";
        }
        if (perfil.isActivo()) {
            return "Tu perfil ya está activo.\nSi deseas desactivarlo, usa 'Solicitar Desactivar Perfil'.";
        }
        return null;
    }

    public void manejarSolicitarReactivar() {
        String error = validarSolicitarReactivar();
        if (error != null) {
            vista.mostrarError(error);
            return;
        }
        if (!vista.confirmar("¿Estás seguro de que deseas solicitar la reactivación de tu perfil?")) {
            return;
        }
        solicitarReactivarPerfil();
    }

    private void solicitarReactivarPerfil() {
        String mensaje   = "CrearSolPer - " + cuentaActual.getEmail() + " -   - restaurar";
        String respuesta = clienteSocket.enviarYRecibir(mensaje);
        procesarRespuestaSolicitud(respuesta, "reactivación");
    }

    // ── RESPUESTA GENÉRICA DE SOLICITUDES ──────────────────
    /**
     * Procesa la respuesta del servidor para solicitudes de baja y restaurar.
     * Se unificó porque el comportamiento es idéntico en ambos casos.
     */
    private void procesarRespuestaSolicitud(String respuesta, String tipoAccion) {
        if (respuesta == null) {
            vista.mostrarError("No se recibió respuesta del servidor.\nIntentá de nuevo.");
            return;
        }

        if (respuesta.toLowerCase().contains("exito")) {
            vista.mostrarMensaje("¡Solicitud de " + tipoAccion + " enviada exitosamente!\n"
                    + "El administrador procesará tu solicitud.");
            vista.limpiarPerfil();
        } else {
            vista.mostrarError("Error del servidor:\n" + respuesta);
        }
    }

    // ── CERRAR SESIÓN ──────────────────────────────────────
    public void manejarCerrarSesion() {
        if (!vista.confirmar("¿Deseas cerrar sesión?")) return;
        vista.dispose();
        vista.navegarALogin(cuentaActual.getEmail(), cuentaActual.getNombreCuenta());
    }

    public CuentaPersonal getCuentaActual() { return cuentaActual; }
    public BancoADN_Grupo6_ClienteSocket getClienteSocket() { return clienteSocket; }
}