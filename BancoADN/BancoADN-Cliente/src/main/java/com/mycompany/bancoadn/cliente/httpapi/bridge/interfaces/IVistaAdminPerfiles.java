package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaAdminPerfiles {
    String getTipoFiltro();
    String getTextoBusqueda();
    void limpiarPerfiles();
    void mostrarSinResultados();
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    boolean confirmar(String msg);
    void agregarTarjetaPerfil(int id, String nombre, String codigo, String descripcion, String fecha, String email, boolean activo, int idPerfilParaAccion);
    void dispose();
}
