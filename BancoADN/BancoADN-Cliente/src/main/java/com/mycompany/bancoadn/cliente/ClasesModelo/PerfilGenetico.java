package com.mycompany.bancoadn.cliente.ClasesModelo;

public class PerfilGenetico {

    private int idPerfil;
    private String nombreCompleto;
    private String codigoSecuencia;
    private String descripcion;
    private int estado; // 1=ACTIVO, 0=INACTIVO
    private String fechaMuestra;
    private int idCuenta; // FK a CuentaPersonal

    public PerfilGenetico(int idPerfil, String nombreCompleto, String codigoSecuencia,
                          String descripcion, int estado, String fechaMuestra, int idCuenta) {
        this.idPerfil = idPerfil;
        this.nombreCompleto = nombreCompleto;
        this.codigoSecuencia = codigoSecuencia;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaMuestra = fechaMuestra;
        this.idCuenta = idCuenta;
    }

    public int getIdPerfil() { return idPerfil; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getCodigoSecuencia() { return codigoSecuencia; }
    public String getDescripcion() { return descripcion; }
    public int getEstado() { return estado; }
    public String getFechaMuestra() { return fechaMuestra; }
    public int getIdCuenta() { return idCuenta; }

    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public void setCodigoSecuencia(String codigoSecuencia) { this.codigoSecuencia = codigoSecuencia; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setEstado(int estado) { this.estado = estado; }
    public void setFechaMuestra(String fechaMuestra) { this.fechaMuestra = fechaMuestra; }

    public boolean isActivo() { return estado == 1; }

    @Override
    public String toString() {
        return "PerfilGenetico{idPerfil=" + idPerfil + ", nombreCompleto=" + nombreCompleto
                + ", estado=" + (estado == 1 ? "ACTIVO" : "INACTIVO") + "}";
    }
}