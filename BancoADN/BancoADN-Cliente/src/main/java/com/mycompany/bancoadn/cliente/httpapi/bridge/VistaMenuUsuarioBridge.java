package com.mycompany.bancoadn.cliente.httpapi.bridge;

import com.mycompany.bancoadn.cliente.httpapi.bridge.interfaces.IVistaMenuUsuario;
import java.util.HashMap;
import java.util.Map;

public class VistaMenuUsuarioBridge implements IVistaMenuUsuario {
    private final BridgeResult result = new BridgeResult();
    private boolean confirmResult = true;

    public void setConfirmResult(boolean v) { this.confirmResult = v; }

    @Override public void mostrarError(String msg) { result.setError(msg); }
    @Override public void mostrarMensaje(String msg) { result.setMessage(msg); result.put("type", "info"); }
    @Override public void mostrarPerfil(String nombre, String codigo, String descripcion, String estado, String fecha) {
        Map<String, Object> perfil = new HashMap<>();
        perfil.put("nombreCompleto", nombre); perfil.put("codigoSecuencia", codigo);
        perfil.put("descripcion", descripcion);
        perfil.put("estado", "ACTIVO".equals(estado) ? 1 : 0);
        perfil.put("fechaMuestra", fecha);
        result.put("perfil", perfil);
    }
    @Override public void limpiarPerfil() { result.put("perfil", null); }
    @Override public boolean confirmar(String msg) { result.put("confirmMsg", msg); return confirmResult; }
    @Override public void dispose() { result.put("disposed", true); }
    @Override public void navegarABuscar() { result.setNavigate("buscar"); }
    @Override public void navegarASolicitarPerfil() { result.setNavigate("solicitarPerfil"); }
    @Override public void navegarASolicitarModificacion() { result.setNavigate("solicitarModificacion"); }
    @Override public void navegarALogin(String email, String nombre) { result.setNavigate("login"); }
    public BridgeResult getResult() { return result; }
}
