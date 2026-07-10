package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaIniciarSesion;

public class VistaIniciarSesionBridge implements IVistaIniciarSesion {
    private final BridgeResult result = new BridgeResult();
    private String email = "";
    private String contraseña = "";

    public void setInputEmail(String v) { this.email = v; }
    public void setInputContraseña(String v) { this.contraseña = v; }

    @Override public String getEmail() { return email; }
    @Override public String getContraseña() { return contraseña; }
    @Override public void mostrarError(String msg) { result.setSuccess(false); result.setMessage(msg); }
    @Override public void mostrarMensaje(String msg) { result.setSuccess(true); result.setMessage(msg); }
    @Override public void limpiarCampos() { /* no-op */ }
    @Override public void dispose() { result.put("disposed", true); }
    @Override public void navegarARegistro() { result.setNavigate("registro"); }
    @Override public void navegarAMenuUsuario(String email, String nombre, int idRol) {
        result.setNavigate("menuUsuario");
        result.put("email", email);
        result.put("nombre", nombre);
        result.put("idRol", idRol);
    }
    @Override public void navegarAMenuAdmin(String email, String nombre, int idRol) {
        result.setNavigate("menuAdmin");
        result.put("email", email);
        result.put("nombre", nombre);
        result.put("idRol", idRol);
    }
    public BridgeResult getResult() { return result; }
}
