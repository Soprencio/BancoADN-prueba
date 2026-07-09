package com.mycompany.bancoadn.cliente.ClasesModelo;

public class Registro {

    private int idRegistro;
    private int idTipoAccion; // FK a TipoAccion
    private int idCuenta;     // FK a CuentaPersonal
    private String fechaRegistro;
    private String detalles;

    public Registro(int idRegistro, int idTipoAccion, int idCuenta,
                    String fechaRegistro, String detalles) {
        this.idRegistro = idRegistro;
        this.idTipoAccion = idTipoAccion;
        this.idCuenta = idCuenta;
        this.fechaRegistro = fechaRegistro;
        this.detalles = detalles;
    }

    public int getIdRegistro() { return idRegistro; }
    public int getIdTipoAccion() { return idTipoAccion; }
    public int getIdCuenta() { return idCuenta; }
    public String getFechaRegistro() { return fechaRegistro; }
    public String getDetalles() { return detalles; }

    public void setDetalles(String detalles) { this.detalles = detalles; }

    @Override
    public String toString() {
        return "Registro{idRegistro=" + idRegistro + ", idTipoAccion=" + idTipoAccion
                + ", idCuenta=" + idCuenta + ", fechaRegistro=" + fechaRegistro + "}";
    }
}