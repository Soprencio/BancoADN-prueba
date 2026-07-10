package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaMenuAdmin {
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    boolean confirmar(String msg);
    void limpiarSolicitudes();
    void mostrarSinSolicitudes();
    void agregarTarjetaSolicitud(int idSolicitud, String mail, String fecha, String tipo, String lineaExtra, int idSolicitudParaAccion, String datosSolicitud);
    void dispose();
    void navegarALogin();
}
