package com.mycompany.bancoadn.cliente.ClasesModelo;

public class CuentaAsignada {

    private int idSolicitud;    // PK + FK a Solicitud
    private int idCuenta;       // PK + FK a CuentaPersonal (admin que resolvió)
    private String fechaResolucion;

    public CuentaAsignada(int idSolicitud, int idCuenta, String fechaResolucion) {
        this.idSolicitud = idSolicitud;
        this.idCuenta = idCuenta;
        this.fechaResolucion = fechaResolucion;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public int getIdCuenta() { return idCuenta; }
    public String getFechaResolucion() { return fechaResolucion; }

    public void setFechaResolucion(String fechaResolucion) { this.fechaResolucion = fechaResolucion; }

    @Override
    public String toString() {
        return "CuentaAsignada{idSolicitud=" + idSolicitud + ", idCuenta=" + idCuenta
                + ", fechaResolucion=" + fechaResolucion + "}";
    }
}
