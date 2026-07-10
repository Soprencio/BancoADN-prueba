package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaLogs {
    void agregarTarjetaLog(int id, String nombre, String email, String descripcion, String fecha, String acciones, boolean esAdmin);
    void limpiarLogs();
    void mostrarSinLogs();
    void mostrarError(String msg);
    void dispose();
}
