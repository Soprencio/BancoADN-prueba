package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaUltimasSolicitudes {
    void agregarTarjetaSolicitud(int id, String mail, String fecha, String fechaResolucion, String tipo, String estadoTexto, String lineaExtra, String datosSolicitud);
    void limpiarSolicitudes();
    void mostrarSinSolicitudes();
    void mostrarError(String msg);
    void dispose();
}
