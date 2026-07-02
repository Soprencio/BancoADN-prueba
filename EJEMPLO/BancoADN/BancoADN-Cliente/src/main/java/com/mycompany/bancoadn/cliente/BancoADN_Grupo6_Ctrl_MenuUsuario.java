/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

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

    private BancoADN_Grupo6_MenuUsuario vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
    private CuentaPersonal cuentaActual; // Credencial de sesión

    public BancoADN_Grupo6_Ctrl_MenuUsuario(BancoADN_Grupo6_MenuUsuario vista,
                                             BancoADN_Grupo6_ClienteSocket clienteSocket,
                                             CuentaPersonal cuentaActual) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.cuentaActual = cuentaActual;
        initListeners();
    }

    private void initListeners() {
        vista.agregarListenerBuscar(e -> manejarBuscar());
        vista.agregarListenerSolicitarPerfil(e -> manejarSolicitarPerfil());
        vista.agregarListenerSolicitarModificacion(e -> manejarSolicitarModificacion());
        vista.agregarListenerSolicitarDesactivar(e -> manejarSolicitarDesactivar());
        vista.agregarListenerSolicitarReactivar(e -> manejarSolicitarReactivar());
        vista.agregarListenerCerrarSesion(e -> manejarCerrarSesion());
        vista.agregarListenerVerPerfil(e -> manejarVerPerfil());
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

    // ── BUSCAR OTROS PERFILES ──────────────────────────────
    private void manejarBuscar() {
        BancoADN_Grupo6_Pant_BuscarPerfil vistaBuscarPerfil = new BancoADN_Grupo6_Pant_BuscarPerfil();
        BancoADN_Grupo6_Ctrl_BuscarPerfil ctrlBuscarPerfil  = new BancoADN_Grupo6_Ctrl_BuscarPerfil(
            vistaBuscarPerfil, clienteSocket, cuentaActual // pasa la credencial de sesión
        );
        vistaBuscarPerfil.setVisible(true);
        vista.dispose();
    }

    // ── VER PERFIL ACTUAL ──────────────────────────────────
    /**
     * Obtiene un snapshot del perfil y lo muestra en el panel derecho.
     * El snapshot muere al finalizar este método.
     */
    private void manejarVerPerfil() {
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

    // ── SOLICITAR REGISTRAR PERFIL ─────────────────────────
    /**
     * REGLA DE NEGOCIO: Solo si el usuario NO tiene perfil (snapshot == null)
     */
    private void manejarSolicitarPerfil() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();

        if (perfil != null) {
            vista.mostrarError("Ya tienes un perfil vinculado a tu cuenta.\n"
                    + "No puedes solicitar un nuevo perfil.");
            return;
        }

        BancoADN_Grupo6_Pant_SolicitarPerfil vistaSolicitarPerfil = new BancoADN_Grupo6_Pant_SolicitarPerfil();
        BancoADN_Grupo6_Ctrl_SolicitarPerfil ctrlSolicitarPerfil  = new BancoADN_Grupo6_Ctrl_SolicitarPerfil(
            vistaSolicitarPerfil, clienteSocket, cuentaActual
        );
        vistaSolicitarPerfil.setVisible(true);
        vista.dispose();
    }

    // ── SOLICITAR MODIFICACIÓN ─────────────────────────────
    /**
     * REGLA DE NEGOCIO: Solo si perfil existe Y está ACTIVO
     * El Ctrl_SolicitarModPerfil hará su propia re-consulta para datos frescos.
     */
    private void manejarSolicitarModificacion() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();

        if (perfil == null) {
            vista.mostrarError("No tienes un perfil registrado.\n"
                    + "Primero debes solicitar un perfil para poder modificarlo.");
            return;
        }

        if (!perfil.isActivo()) {
            vista.mostrarError("Tu perfil está desactivado.\n"
                    + "Debes restaurarlo antes de poder modificarlo.");
            return;
        }

        // Solo pasamos la credencial de sesión, NO el perfil
        // Ctrl_SolicitarModPerfil hará su propia re-consulta para datos frescos
        BancoADN_Grupo6_Pant_SolicitarModPerfil vistaMod = new BancoADN_Grupo6_Pant_SolicitarModPerfil();
        BancoADN_Grupo6_Ctrl_SolicitarModPerfil ctrlMod  = new BancoADN_Grupo6_Ctrl_SolicitarModPerfil(
            vistaMod, clienteSocket, cuentaActual
        );
        vistaMod.setVisible(true);
        vista.dispose();
    }

    // ── SOLICITAR DESACTIVAR ───────────────────────────────
    /**
     * REGLA DE NEGOCIO: Solo si perfil existe Y está ACTIVO
     */
    private void manejarSolicitarDesactivar() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();

        if (perfil == null) {
            vista.mostrarError("No tienes un perfil registrado.\nNo hay nada que desactivar.");
            return;
        }

        if (!perfil.isActivo()) {
            vista.mostrarError("Tu perfil ya está desactivado.\n"
                    + "Si deseas reactivarlo, usa el botón 'Solicitar Reactivar Perfil'.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista,
            "¿Estás seguro de que deseas solicitar la desactivación de tu perfil?",
            "Confirmar Solicitud",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            solicitarDesactivarPerfil();
        }
    }

    private void solicitarDesactivarPerfil() {
        String mensaje   = "CrearSolPer - " + cuentaActual.getEmail() + " -   - baja";
        String respuesta = clienteSocket.enviarYRecibir(mensaje);
        procesarRespuestaSolicitud(respuesta, "desactivación");
    }

    // ── SOLICITAR REACTIVAR ────────────────────────────────
    /**
     * REGLA DE NEGOCIO: Solo si perfil existe Y está INACTIVO
     */
    private void manejarSolicitarReactivar() {
        PerfilGenetico perfil = obtenerSnapshotPerfil();

        if (perfil == null) {
            vista.mostrarError("No tienes un perfil registrado.\n"
                    + "Usa el botón 'Solicitar Perfil' para crear uno.");
            return;
        }

        if (perfil.isActivo()) {
            vista.mostrarError("Tu perfil ya está activo.\n"
                    + "Si deseas desactivarlo, usa el botón 'Solicitar Desactivar Perfil'.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista,
            "¿Estás seguro de que deseas solicitar la reactivación de tu perfil?",
            "Confirmar Solicitud",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            solicitarReactivarPerfil();
        }
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
    private void manejarCerrarSesion() {
        int opc = JOptionPane.showConfirmDialog(vista,
            "¿Deseas cerrar sesión?",
            "Salir",
            JOptionPane.YES_NO_OPTION);

        if (opc == JOptionPane.YES_OPTION) {
            vista.dispose();
            BancoADN_Grupo6_Pant_IniciarSesion loginVista = new BancoADN_Grupo6_Pant_IniciarSesion();
            BancoADN_Grupo6_Ctrl_IniciarSesion ctrlLogin  = new BancoADN_Grupo6_Ctrl_IniciarSesion(loginVista, clienteSocket);
            loginVista.setVisible(true);
        }
    }
}