package com.mycompany.bancoadn.cliente.ClasesModelo;
 
public class TipoAccion {
 
    private int idTipoAccion;
    private String nombreAccion;
 
    public TipoAccion(int idTipoAccion, String nombreAccion) {
        this.idTipoAccion = idTipoAccion;
        this.nombreAccion = nombreAccion;
    }
 
    public int getIdTipoAccion() { return idTipoAccion; }
    public String getNombreAccion() { return nombreAccion; }
 
    public void setNombreAccion(String nombreAccion) { this.nombreAccion = nombreAccion; }
 
    @Override
    public String toString() {
        return "TipoAccion{idTipoAccion=" + idTipoAccion + ", nombreAccion=" + nombreAccion + "}";
    }
}
