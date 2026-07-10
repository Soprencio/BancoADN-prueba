package com.mycompany.bancoadn.cliente;

import java.util.List;
import java.util.ArrayList;
import com.mycompany.bancoadn.cliente.ClasesModelo.CuentaPersonal;
import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;
import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaAdminPerfiles;

public class BancoADN_Grupo6_Ctrl_AdminPerfiles {

    private final IVistaAdminPerfiles vista;
    private final BancoADN_Grupo6_ClienteSocket clienteSocket;
    private final CuentaPersonal adminLogueado;

    public BancoADN_Grupo6_Ctrl_AdminPerfiles(IVistaAdminPerfiles vista,
                              BancoADN_Grupo6_ClienteSocket clienteSocket,
                              CuentaPersonal adminLogueado) {
        this.vista         = vista;
        this.clienteSocket = clienteSocket;
        this.adminLogueado    = adminLogueado;
    }

    public void ejecutarBusqueda(String filtro, String texto) {
        vista.limpiarPerfiles();

        if (filtro == null) filtro = "Todos";
        if (texto == null) texto = "";

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
                perfil.getIdPerfil()
            );
        }
    }

    public void solicitarBajaPerfil(String emailTitular) {
        if (!vista.confirmar("¿Confirmás dar de baja el perfil de " + emailTitular + "?")) return;

        String respuesta = clienteSocket.enviarYRecibir("DarDBaja - " + emailTitular);
        if (respuesta != null && respuesta.toLowerCase().contains("true")) {
            vista.mostrarMensaje("Perfil dado de baja correctamente.");
        } else {
            vista.mostrarError("No se pudo dar de baja el perfil.");
        }
    }

    public void solicitarRestaurarPerfil(String emailTitular) {
        if (!vista.confirmar("¿Confirmás restaurar el perfil de " + emailTitular + "?")) return;

        String respuesta = clienteSocket.enviarYRecibir("DarDRestaur - " + emailTitular);
        if (respuesta != null && respuesta.toLowerCase().contains("true")) {
            vista.mostrarMensaje("Perfil restaurado correctamente.");
        } else {
            vista.mostrarError("No se pudo restaurar el perfil.");
        }
    }

    public void abrirModificarPerfil(String emailTitular, int idPerfil) {
        List<PerfilGenetico> perfiles = obtenerSnapshotPerfiles("ID", String.valueOf(idPerfil));
        if (perfiles.isEmpty()) {
            vista.mostrarError("No se encontró el perfil.");
            return;
        }
        PerfilGenetico perfil = perfiles.get(0);
        vista.mostrarMensaje("MODIFICAR:" + idPerfil + "|" + emailTitular + "|"
            + perfil.getNombreCompleto() + "|" + perfil.getCodigoSecuencia() + "|"
            + perfil.getDescripcion() + "|" + perfil.getFechaMuestra());
    }

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
                    Integer.parseInt(p[0].trim()),
                    p[1].trim(),
                    p[2].trim(),
                    p[3].trim(),
                    Integer.parseInt(p[4].trim()),
                    p[5].trim(),
                    -1
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
}
