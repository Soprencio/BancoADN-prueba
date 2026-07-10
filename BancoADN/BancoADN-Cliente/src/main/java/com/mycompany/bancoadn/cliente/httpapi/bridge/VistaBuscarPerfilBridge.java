package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaBuscarPerfil;
import java.util.HashMap;
import java.util.Map;

public class VistaBuscarPerfilBridge implements IVistaBuscarPerfil {
    private final BridgeResult result = new BridgeResult();
    private String tipoFiltro = "Nombre";
    private String textoBusqueda = "";

    public void setInputTipoFiltro(String v) { this.tipoFiltro = v; }
    public void setInputTextoBusqueda(String v) { this.textoBusqueda = v; }

    @Override public String getTipoFiltro() { return tipoFiltro; }
    @Override public String getTextoBusqueda() { return textoBusqueda; }
    @Override public void limpiarResultados() { result.put("resultados", new java.util.ArrayList<>()); }
    @Override public void agregarResultado(String id, String nombre, String codigo, String desc, String fecha) {
        Map<String, Object> r = new HashMap<>();
        r.put("idPerfil", Integer.parseInt(id));
        r.put("nombreCompleto", nombre); r.put("codigoSecuencia", codigo);
        r.put("descripcion", desc); r.put("fechaMuestra", fecha);
        r.put("estado", 1);
        result.addCard("resultados", r);
    }
    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void mostrarMensaje(String msg) { result.setSuccess(true); result.setMessage(msg); }
    @Override public void dispose() { result.put("disposed", true); }
    @Override public void navegarAMenu() { result.setNavigate("menuUsuario"); }
    public BridgeResult getResult() { return result; }
}
