package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaAdminPerfiles;
import java.util.HashMap;
import java.util.Map;

public class VistaAdminPerfilesBridge implements IVistaAdminPerfiles {
    private final BridgeResult result = new BridgeResult();
    private boolean confirmResult = true;
    private String tipoFiltro = "Todos", textoBusqueda = "";

    public void setInputTipoFiltro(String v) { this.tipoFiltro = v; }
    public void setInputTextoBusqueda(String v) { this.textoBusqueda = v; }
    public void setConfirmResult(boolean v) { this.confirmResult = v; }

    @Override public String getTipoFiltro() { return tipoFiltro; }
    @Override public String getTextoBusqueda() { return textoBusqueda; }
    @Override public void limpiarPerfiles() { result.put("perfiles", new java.util.ArrayList<>()); }
    @Override public void mostrarSinResultados() { result.put("perfilesVacio", true); }
    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void mostrarMensaje(String msg) { result.setSuccess(true); result.setMessage(msg); }
    @Override public boolean confirmar(String msg) { result.put("confirmMsg", msg); return confirmResult; }
    @Override public void agregarTarjetaPerfil(int id, String nombre, String codigo, String desc, String fecha, String email, boolean activo, int idAccion) {
        Map<String, Object> card = new HashMap<>();
        card.put("idPerfil", id);
        card.put("nombreCompleto", nombre); card.put("codigoSecuencia", codigo);
        card.put("descripcion", desc); card.put("fechaMuestra", fecha);
        card.put("email", email); card.put("estado", activo ? 1 : 0);
        result.addCard("perfiles", card);
    }
    @Override public void dispose() { result.put("disposed", true); }
    public BridgeResult getResult() { return result; }
}
