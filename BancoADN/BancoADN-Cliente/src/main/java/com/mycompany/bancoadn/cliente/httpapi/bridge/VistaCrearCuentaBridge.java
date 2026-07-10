package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaCrearCuenta;

public class VistaCrearCuentaBridge implements IVistaCrearCuenta {
    private final BridgeResult result = new BridgeResult();
    private String nombre = "", email = "", contraseña = "";

    public void setInputNombre(String v) { this.nombre = v; }
    public void setInputEmail(String v) { this.email = v; }
    public void setInputContraseña(String v) { this.contraseña = v; }

    @Override public String getNombreCuenta() { return nombre; }
    @Override public String getEmail() { return email; }
    @Override public String getContraseña() { return contraseña; }
    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void mostrarMensaje(String msg) { result.setSuccess(true); result.setMessage(msg); }
    @Override public void limpiarCampos() { /* no-op */ }
    @Override public void dispose() { result.put("disposed", true); }
    @Override public void navegarALogin() { result.setNavigate("login"); }
    public BridgeResult getResult() { return result; }
}
