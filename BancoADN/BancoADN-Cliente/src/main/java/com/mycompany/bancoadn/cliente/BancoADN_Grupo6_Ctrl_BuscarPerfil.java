/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;

/**
 * Controlador para la pantalla de Buscar Perfil.
 *
 * CAPA DE MODELO:
 *   Por cada línea que devuelve el servidor se crea un objeto PerfilGenetico.
 *   Los resultados se almacenan en un List<PerfilGenetico> mientras dura la búsqueda.
 *   Al hacer click en "Ver Perfil" se usan los getters del objeto en lugar de un Map<String, String>.
 *
 * Protocolo servidor:
 *   BuscarIDNOM - NULL - nombre
 *   BuscarIDNOM - id - NULL
 *   Respuesta: idPerfil - nombreCompleto - códigoSecuencia - descripción - estado - fechaMuestra - email
 *   Termina con: FINISH
 */
public class BancoADN_Grupo6_Ctrl_BuscarPerfil {

    private BancoADN_Grupo6_Pant_BuscarPerfil vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
    private CuentaPersonal cuentaActual;

    // Lista de snapshots de la búsqueda actual
    // Cada PerfilGenetico se vincula con un botón "Ver Perfil" por índice
    private List<PerfilGenetico> resultadosActuales = new ArrayList<>();

    public BancoADN_Grupo6_Ctrl_BuscarPerfil(BancoADN_Grupo6_Pant_BuscarPerfil vista,
                                               BancoADN_Grupo6_ClienteSocket clienteSocket,
                                               CuentaPersonal cuentaActual) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.cuentaActual = cuentaActual;
        this.vista.limpiarResultados();
        initListeners();
    }

    private void initListeners() {
        this.vista.btnAtras.addActionListener(e -> manejarVolver());
        this.vista.btnBuscar.addActionListener(e -> manejarBuscar());
    }

    // ── VOLVER AL MENÚ ───────────────────────────────────────
    private void manejarVolver() {
        vista.dispose();
        BancoADN_Grupo6_MenuUsuario menuVista = new BancoADN_Grupo6_MenuUsuario();
        menuVista.setEmailUsuario(cuentaActual.getEmail());
        menuVista.setNombreUsuario(cuentaActual.getNombreCuenta());
        BancoADN_Grupo6_Ctrl_MenuUsuario ctrlMenuUsuario = new BancoADN_Grupo6_Ctrl_MenuUsuario(
            menuVista, clienteSocket, cuentaActual
        );
        menuVista.setVisible(true);
    }

    // ── BUSCAR PERFILES ──────────────────────────────────────
    private void manejarBuscar() {
        String tipoFiltro = vista.getTipoFiltro();
        String texto      = vista.getTextoBusqueda().trim();

        if (texto.isEmpty() || texto.equals("Buscar por ID / Nombre")) {
            vista.mostrarError("Por favor ingresa un ID o un Nombre para buscar.");
            return;
        }

        String mensaje;
        if (tipoFiltro.equals("Nombre")) {
            mensaje = "BuscarIDNOM - NULL - " + texto;
        } else {
            if (!texto.matches("\\d+")) {
                vista.mostrarError("El ID de perfil debe ser un valor numérico.");
                return;
            }
            mensaje = "BuscarIDNOM - " + texto + " - NULL";
        }

        vista.limpiarResultados();
        resultadosActuales.clear(); // Limpiar snapshots anteriores

        List<String> respuestas = clienteSocket.enviarYSolicitarLista(mensaje);
        procesarRespuesta(respuestas);
    }

    /**
     * Por cada línea válida del servidor se construye un PerfilGenetico.
     * Solo se agregan a la vista y a la lista los perfiles ACTIVOS (estado = 1).
     *
     * Formato por línea:
     *   idPerfil(0) - nombreCompleto(1) - códigoSecuencia(2) - descripción(3) - estado(4) - fechaMuestra(5) - email(6)
     */
    private void procesarRespuesta(List<String> respuestas) {
        if (respuestas == null) {
            vista.mostrarError("Error al conectar con el servidor.\nVerificá que el servidor esté activo.");
            return;
        }

        if (respuestas.isEmpty()) {
            vista.mostrarError("No se encontraron perfiles que coincidan con la búsqueda.");
            return;
        }

        int perfilesActivos = 0;

        for (String linea : respuestas) {
            if (linea.equals("FINISH")) continue;

            String[] partes = linea.split(" - ");

            if (linea.equals("No se encontro el perfil") ||
                (partes.length > 0 && partes[0].equals("Comando Invalido bot"))) {
                vista.mostrarError(linea);
                vista.limpiarResultados();
                return;
            }

            if (partes.length >= 6) {
                // Construir snapshot PerfilGenetico desde la línea cruda
                PerfilGenetico perfil = new PerfilGenetico(
                    Integer.parseInt(partes[0].trim()), // idPerfil
                    partes[1].trim(),                   // nombreCompleto
                    partes[2].trim(),                   // codigoSecuencia
                    partes[3].trim(),                   // descripcion
                    Integer.parseInt(partes[4].trim()), // estado: 1=ACTIVO, 0=INACTIVO
                    partes[5].trim(),                   // fechaMuestra
                    -1                                  // idCuenta: no disponible en esta consulta
                );

                // FILTRO: solo mostrar activos
                if (perfil.isActivo()) {
                    resultadosActuales.add(perfil);
                    vista.agregarResultado(
                        String.valueOf(perfil.getIdPerfil()),
                        perfil.getNombreCompleto(),
                        perfil.getCodigoSecuencia(),
                        perfil.getDescripcion(),
                        perfil.getFechaMuestra()
                    );
                    perfilesActivos++;
                }

            } else {
                vista.mostrarError("Respuesta inesperada del servidor:\n" + linea);
            }
        }

        // Vincular cada botón "Ver Perfil" con el índice de resultadosActuales
        vista.configurarAccionesVerPerfil(e -> manejarVerDetalle((JButton) e.getSource()));

        if (perfilesActivos == 0) {
            vista.mostrarError("No se encontraron perfiles ACTIVOS que coincidan con la búsqueda.\n"
                    + "(Los perfiles desactivados no se muestran)");
        }
    }

    /**
     * Al hacer click en "Ver Perfil" se recupera el PerfilGenetico correspondiente
     * usando el índice del botón en la lista de botones de la vista.
     * Se usan getters del objeto en lugar del Map<String, String> anterior.
     */
    private void manejarVerDetalle(JButton boton) {
        int indice = vista.getBotonesVer().indexOf(boton);

        if (indice == -1 || indice >= resultadosActuales.size()) {
            vista.mostrarError("No se pudieron recuperar los datos del perfil.");
            return;
        }

        PerfilGenetico perfil = resultadosActuales.get(indice);

        String detalle = "<html>" +
            "<body style='width: 300px; font-family: sans-serif;'>" +
            "<h2 style='color: #4A90E2;'>Detalles del Perfil</h2>" +
            "<hr>" +
            "<b>ID del Perfil:</b> "        + perfil.getIdPerfil()        + "<br><br>" +
            "<b>Nombre Completo:</b> "       + perfil.getNombreCompleto()  + "<br><br>" +
            "<b>Código de Secuencia:</b><br>" +
            "<div style='background: #eeeeee; padding: 5px;'>" + perfil.getCodigoSecuencia() + "</div><br>" +
            "<b>Fecha de Muestra:</b> "      + perfil.getFechaMuestra()    + "<br><br>" +
            "<b>Descripción:</b><br><i>"     + perfil.getDescripcion()     + "</i>" +
            "</body></html>";

        vista.mostrarMensaje(detalle);
    }
}