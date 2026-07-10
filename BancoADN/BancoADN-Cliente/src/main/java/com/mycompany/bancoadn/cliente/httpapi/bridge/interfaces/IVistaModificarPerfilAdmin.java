package com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces;

public interface IVistaModificarPerfilAdmin {
    String getNombrePerfil();
    String getCodigoSecuencia();
    String getDescripcion();
    String getFechaMuestra();
    void mostrarError(String msg);
    void mostrarMensaje(String msg);
    void dispose();
}
