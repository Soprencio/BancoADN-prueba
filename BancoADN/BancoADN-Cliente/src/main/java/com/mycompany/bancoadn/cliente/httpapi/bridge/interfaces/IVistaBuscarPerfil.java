package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

import java.util.List;

public interface IVistaBuscarPerfil {
    String getTipoFiltro();
    String getTextoBusqueda();
    void limpiarResultados();
    void agregarResultado(String id, String nombre, String codigo, String descripcion, String fecha);
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    void dispose();
    void navegarAMenu();
}
