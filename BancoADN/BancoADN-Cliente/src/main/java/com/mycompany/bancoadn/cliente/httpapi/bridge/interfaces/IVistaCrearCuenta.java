package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaCrearCuenta {
    String getNombreCuenta();
    String getEmail();
    String getContraseña();
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void dispose();
    void navegarALogin();
}
