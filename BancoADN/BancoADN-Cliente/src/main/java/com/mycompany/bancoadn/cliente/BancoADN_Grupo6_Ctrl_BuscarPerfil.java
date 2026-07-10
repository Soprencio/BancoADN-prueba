package com.mycompany.bancoadn.cliente;

import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaBuscarPerfil;
import java.util.ArrayList;
import java.util.List;

public class BancoADN_Grupo6_Ctrl_BuscarPerfil {

    private IVistaBuscarPerfil vista;
    private BancoADN_Grupo6_ClienteSocket clienteSocket;
    private CuentaPersonal cuentaActual;
    private List<PerfilGenetico> resultadosActuales = new ArrayList<>();

    public BancoADN_Grupo6_Ctrl_BuscarPerfil(IVistaBuscarPerfil vista,
                                               BancoADN_Grupo6_ClienteSocket clienteSocket,
                                               CuentaPersonal cuentaActual) {
        this.vista = vista;
        this.clienteSocket = clienteSocket;
        this.cuentaActual = cuentaActual;
    }

    public void ejecutarBusqueda(String tipoFiltro, String texto) {
        if (texto == null) texto = "";
        texto = texto.trim();
        if (tipoFiltro == null) tipoFiltro = "Todos";

        String mensaje;
        if ("Todos".equals(tipoFiltro) && texto.isEmpty()) {
            mensaje = "BuscarIDNOM - NULL - NULL";
        } else if (texto.isEmpty()) {
            vista.mostrarError("Por favor ingresa un ID o un Nombre para buscar.");
            return;
        } else if ("Nombre".equalsIgnoreCase(tipoFiltro)) {
            mensaje = "BuscarIDNOM - NULL - " + texto;
        } else {
            if (!texto.matches("\\d+")) {
                vista.mostrarError("El ID de perfil debe ser un valor numérico.");
                return;
            }
            mensaje = "BuscarIDNOM - " + texto + " - NULL";
        }

        vista.limpiarResultados();
        resultadosActuales.clear();

        List<String> respuestas = clienteSocket.enviarYSolicitarLista(mensaje);
        procesarRespuesta(respuestas);
    }

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
                return;
            }

            if (partes.length >= 6) {
                PerfilGenetico perfil = new PerfilGenetico(
                    Integer.parseInt(partes[0].trim()),
                    partes[1].trim(),
                    partes[2].trim(),
                    partes[3].trim(),
                    Integer.parseInt(partes[4].trim()),
                    partes[5].trim(),
                    -1
                );

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

        if (perfilesActivos == 0) {
            vista.mostrarError("No se encontraron perfiles ACTIVOS que coincidan con la búsqueda.\n"
                    + "(Los perfiles desactivados no se muestran)");
        }
    }

    public void manejarVolver() {
        vista.dispose();
        vista.navegarAMenu();
    }
}
