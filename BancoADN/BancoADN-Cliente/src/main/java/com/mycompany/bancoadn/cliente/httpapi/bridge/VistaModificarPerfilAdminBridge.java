package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaModificarPerfilAdmin;

public class VistaModificarPerfilAdminBridge implements IVistaModificarPerfilAdmin {
    private final BridgeResult result = new BridgeResult();
    private String nombre = "", codigo = "", descripcion = "", fecha = "";

    public void setInputNombre(String v) { this.nombre = v; }
    public void setInputCodigo(String v) { this.codigo = v; }
    public void setInputDescripcion(String v) { this.descripcion = v; }
    public void setInputFecha(String v) { this.fecha = v; }

    @Override public String getNombrePerfil() { return nombre; }
    @Override public String getCodigoSecuencia() { return codigo; }
    @Override public String getDescripcion() { return descripcion; }
    @Override public String getFechaMuestra() { return fecha; }
    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void mostrarMensaje(String msg) { result.setSuccess(true); result.setMessage(msg); }
    @Override public void dispose() { result.put("disposed", true); }
    public BridgeResult getResult() { return result; }
}
