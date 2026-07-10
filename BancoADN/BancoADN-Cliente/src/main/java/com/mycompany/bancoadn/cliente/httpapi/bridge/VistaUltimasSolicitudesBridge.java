package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaUltimasSolicitudes;
import java.util.HashMap;
import java.util.Map;

public class VistaUltimasSolicitudesBridge implements IVistaUltimasSolicitudes {
    private final BridgeResult result = new BridgeResult();

    @Override public void agregarTarjetaSolicitud(int id, String mail, String fecha, String fechaRes, String tipo, String estadoTexto, String linea, String datosSolicitud) {
        Map<String, Object> card = new HashMap<>();
        int estado = estadoTexto != null && estadoTexto.toLowerCase().contains("aceptada") ? 1 : 2;
        card.put("idSolicitud", id); card.put("email", mail); card.put("fecha", fecha);
        card.put("fechaResolucion", fechaRes); card.put("tipo", tipo);
        card.put("estado", estado);
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
    @Override public void limpiarSolicitudes() { result.put("solicitudes", new java.util.ArrayList<>()); }
    @Override public void mostrarSinSolicitudes() { result.put("solicitudesVacio", true); }
    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void dispose() { result.put("disposed", true); }
    public BridgeResult getResult() { return result; }
}
