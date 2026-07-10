package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaMenuAdmin;
import java.util.HashMap;
import java.util.Map;

public class VistaMenuAdminBridge implements IVistaMenuAdmin {
    private final BridgeResult result = new BridgeResult();
    private boolean confirmResult = true;

    public void setConfirmResult(boolean v) { this.confirmResult = v; }

    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void mostrarMensaje(String msg) { result.setMessage(msg); result.put("type", "info"); }
    @Override public boolean confirmar(String msg) { result.put("confirmMsg", msg); return confirmResult; }
    @Override public void limpiarSolicitudes() { result.put("solicitudes", new java.util.ArrayList<>()); }
    @Override public void mostrarSinSolicitudes() { result.put("solicitudesVacio", true); }
    @Override public void agregarTarjetaSolicitud(int id, String mail, String fecha, String tipo, String linea, int idAccion, String datosSolicitud) {
        Map<String, Object> card = new HashMap<>();
        card.put("idSolicitud", id); card.put("email", mail); card.put("fecha", fecha);
        card.put("tipo", tipo); card.put("estado", 0);
        if (datosSolicitud != null && !datosSolicitud.isEmpty()) {
            String[] t = datosSolicitud.split(" _ ", -1);
            card.put("nombreCompleto", t.length > 1 ? t[1].trim() : "");
            card.put("codigoSecuencia", t.length > 2 ? t[2].trim() : "");
            card.put("descripcion", t.length > 3 ? t[3].trim() : "");
            card.put("fechaMuestra", t.length > 4 ? t[4].trim() : "");
        } else {
            card.put("nombreCompleto", ""); card.put("codigoSecuencia", "");
            card.put("descripcion", linea); card.put("fechaMuestra", "");
        }
        result.addCard("solicitudes", card);
    }
    @Override public void dispose() { result.put("disposed", true); }
    @Override public void navegarALogin() { result.setNavigate("login"); }
    public BridgeResult getResult() { return result; }
}
