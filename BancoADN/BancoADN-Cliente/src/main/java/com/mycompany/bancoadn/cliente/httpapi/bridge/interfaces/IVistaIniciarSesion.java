package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaIniciarSesion {
    String getEmail();
    String getContraseña();
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void dispose();
    void navegarARegistro();
    void navegarAMenuUsuario(String email, String nombre, int idRol);
    void navegarAMenuAdmin(String email, String nombre, int idRol);
}
