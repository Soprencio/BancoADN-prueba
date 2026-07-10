package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaSolicitarPerfil {
    String getNombrePerfil();
    String getCodigoSecuencia();
    String getDescripcion();
    String getFechaMuestra();
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    void limpiarCampos();
    void dispose();
    void navegarAMenu();
}
