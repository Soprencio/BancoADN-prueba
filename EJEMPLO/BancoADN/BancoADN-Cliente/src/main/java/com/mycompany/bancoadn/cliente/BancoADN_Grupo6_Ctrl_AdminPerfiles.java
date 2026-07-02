package com.mycompany.bancoadn.cliente;

import java.util.List;
import java.util.ArrayList;
import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;

public class BancoADN_Grupo6_Ctrl_AdminPerfiles {

    private final BancoADN_Grupo6_Pant_AdminPerfiles vista;
    private final BancoADN_Grupo6_ClienteSocket      clienteSocket;
    private final CuentaPersonal adminLogueado; // Credencial de sesión

    public BancoADN_Grupo6_Ctrl_AdminPerfiles(BancoADN_Grupo6_Pant_AdminPerfiles vista,
                              BancoADN_Grupo6_ClienteSocket clienteSocket,
                              CuentaPersonal adminLogueado) {
        this.vista         = vista;
        this.clienteSocket = clienteSocket;
        this.adminLogueado    = adminLogueado;

        initListeners();
        buscarPerfilesPorCriterio("Todos", "");   // Carga inicial
    }

    private void initListeners() {
        vista.agregarListenerVolver(e -> vista.dispose());
        vista.agregarListenerBuscar(e -> {
            String filtro = vista.getTipoFiltro();
            String texto  = vista.getTextoBusqueda();
            buscarPerfilesPorCriterio(filtro, texto);
        });
    }

    // ════════════════════════════════════════════════════════
    // ── LÓGICA DE CONTROL (Alineada con Diagramas) ─────────
    // ════════════════════════════════════════════════════════

    /**
     * Buscar perfiles. Corresponde a "buscarPerfilesPorCriterio" en el Diagrama de Clases.
     */
    private void buscarPerfilesPorCriterio(String filtro, String texto) {
        vista.limpiarPerfiles();

        if (filtro.equals("ID") && !texto.isEmpty()) {
            try {
                Integer.parseInt(texto);
            } catch (NumberFormatException ex) {
                vista.mostrarError("Formato incorrecto: para buscar por ID debés ingresar solo números.");
                return;
            }
        }

        List<PerfilGenetico> perfiles = obtenerSnapshotPerfiles(filtro, texto);

        if (perfiles.isEmpty()) {
            vista.mostrarSinResultados();
            return;
        }

        for (PerfilGenetico perfil : perfiles) {
            String emailTitular = obtenerEmailTitular(perfil.getIdPerfil());

            vista.agregarTarjetaPerfil(
                perfil.getIdPerfil(), 
                perfil.getNombreCompleto(), 
                perfil.getCodigoSecuencia(), 
                perfil.getDescripcion(), 
                perfil.getFechaMuestra(), 
                emailTitular, 
                perfil.isActivo(),
                e -> abrirModificarPerfil(emailTitular, perfil),
                e -> {
                    if (perfil.isActivo()) {
                        solicitarBajaPerfil(emailTitular);
                    } else {
                        solicitarRestaurarPerfil(emailTitular);
                    }
                }
            );
        }
    }

    /**
     * Solicitar baja. Corresponde a "solicitarBajaPerfil" en el Diagrama de Clases.
     */
    private void solicitarBajaPerfil(String emailTitular) {
        if (!vista.confirmar("¿Confirmás dar de baja el perfil de " + emailTitular + "?")) return;

        String respuesta = clienteSocket.enviarYRecibir("DarDBaja - " + emailTitular);
        if (respuesta != null && respuesta.toLowerCase().contains("true")) {
            vista.mostrarMensaje("Perfil dado de baja correctamente.");
        } else {
            vista.mostrarError("No se pudo dar de baja el perfil.");
        }
        buscarPerfilesPorCriterio(vista.getTipoFiltro(), vista.getTextoBusqueda());
    }

    /**
     * Solicitar restauración. Corresponde a "solicitarRestaurarPerfil" en el Diagrama de Clases.
     */
    private void solicitarRestaurarPerfil(String emailTitular) {
        if (!vista.confirmar("¿Confirmás restaurar el perfil de " + emailTitular + "?")) return;

        String respuesta = clienteSocket.enviarYRecibir("DarDRestaur - " + emailTitular);
        if (respuesta != null && respuesta.toLowerCase().contains("true")) {
            vista.mostrarMensaje("Perfil restaurado correctamente.");
        } else {
            vista.mostrarError("No se pudo restaurar el perfil.");
        }
        buscarPerfilesPorCriterio(vista.getTipoFiltro(), vista.getTextoBusqueda());
    }

    // ════════════════════════════════════════════════════════
    // ── SNAPSHOTS Y SOPORTE ────────────────────────────────
    // ════════════════════════════════════════════════════════

    private List<PerfilGenetico> obtenerSnapshotPerfiles(String filtro, String texto) {
        if (!clienteSocket.estaConectado()) {
            vista.mostrarError("Error de conexión con el servidor.");
            return new ArrayList<>();
        }

        String comando;
        if (filtro.equals("ID") && !texto.isEmpty()) {
            comando = "BuscarIDNOM - " + texto + " - NULL";
        } else if (filtro.equals("Nombre") && !texto.isEmpty()) {
            comando = "BuscarIDNOM - NULL - " + texto;
        } else {
            // "Todos" → Enviar NULL en ambos para que el servidor devuelva todo
            comando = "BuscarIDNOM - NULL - NULL";
        }

        List<String> lineas = clienteSocket.enviarYSolicitarLista(comando);
        List<PerfilGenetico> lista = new ArrayList<>();

        if (lineas == null) return lista;

        for (String linea : lineas) {
            if (linea.startsWith("No se encontro")) continue;
            
            String[] p = linea.split(" - ", -1);
            if (p.length < 7) continue;

            try {
                PerfilGenetico perfil = new PerfilGenetico(
                    Integer.parseInt(p[0].trim()), // idPerfil
                    p[1].trim(),                   // nombreCompleto
                    p[2].trim(),                   // codigoSecuencia
                    p[3].trim(),                   // descripcion
                    Integer.parseInt(p[4].trim()), // estado: 1=ACTIVO, 0=INACTIVO
                    p[5].trim(),                   // fechaMuestra
                    -1                             // idCuenta
                );
                lista.add(perfil);
            } catch (NumberFormatException e) {
                System.err.println("Error al parsear perfil: " + linea);
            }
        }
        return lista;
    }

    private String obtenerEmailTitular(int idPerfil) {
        String respuesta = clienteSocket.enviarYRecibir("EmailPorPerfil - " + idPerfil);
        return (respuesta == null || respuesta.isBlank()) ? "—" : respuesta.trim();
    }

    // ── Abrir ventana modificar directo ───────────────────
    private void abrirModificarPerfil(String emailTitular, PerfilGenetico perfil) {
        BancoADN_Grupo6_Pant_ModificarPerfilAdmin vistaModif = new BancoADN_Grupo6_Pant_ModificarPerfilAdmin();
        vistaModif.setNombrePerfil(perfil.getNombreCompleto());
        vistaModif.setCodigoSecuencia(perfil.getCodigoSecuencia());
        vistaModif.setDescripcion(perfil.getDescripcion());
        vistaModif.setFechaMuestra(perfil.getFechaMuestra());

        new BancoADN_Grupo6_Ctrl_ModificarPerfilAdmin(vistaModif, clienteSocket, emailTitular, adminLogueado, vista);
        vistaModif.setVisible(true);
    }
}
