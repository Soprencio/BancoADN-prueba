package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaSolicitarPerfil;

public class VistaSolicitarPerfilBridge implements IVistaSolicitarPerfil {
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
    @Override public void limpiarCampos() { /* no-op */ }
    @Override public void dispose() { result.put("disposed", true); }
    @Override public void navegarAMenu() { result.setNavigate("menuUsuario"); }
    public BridgeResult getResult() { return result; }
}
