package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaLogs;
import java.util.HashMap;
import java.util.Map;

public class VistaLogsBridge implements IVistaLogs {
    private final BridgeResult result = new BridgeResult();

    @Override public void agregarTarjetaLog(int id, String nombre, String email, String desc, String fecha, String acciones, boolean esAdmin) {
        Map<String, Object> log = new HashMap<>();
        log.put("idRegistro", id); log.put("nombreCuenta", nombre); log.put("email", email);
        log.put("descripcion", desc); log.put("fecha", fecha); log.put("acciones", acciones);
        log.put("esAdmin", esAdmin);
        result.addCard("logs", log);
    }
    @Override public void limpiarLogs() { result.put("logs", new java.util.ArrayList<>()); }
    @Override public void mostrarSinLogs() { result.put("logsVacio", true); }
    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void dispose() { result.put("disposed", true); }
    public BridgeResult getResult() { return result; }
}
