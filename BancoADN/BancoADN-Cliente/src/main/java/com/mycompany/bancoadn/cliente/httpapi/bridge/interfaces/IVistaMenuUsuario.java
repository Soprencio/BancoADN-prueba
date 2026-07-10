package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaMenuUsuario {
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    void mostrarPerfil(String nombre, String codigo, String descripcion, String estado, String fecha);
    void limpiarPerfil();
    boolean confirmar(String msg);
    void dispose();
    void navegarABuscar();
    void navegarASolicitarPerfil();
    void navegarASolicitarModificacion();
    void navegarALogin(String email, String nombre);
}
