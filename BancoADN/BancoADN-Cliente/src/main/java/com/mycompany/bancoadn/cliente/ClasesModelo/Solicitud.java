package com.mycompany.bancoadn.cliente.ClasesModelo;

public class Solicitud {

    private int idSolicitud;
    private String tipo;       // REGISTRAR, MODIFICAR, BAJA, RESTAURAR
    private int estado;        // 0=PENDIENTE, 1=APROBADA, 2=RECHAZADA
    private String datosSolicitud;
    private int idPerfil;      // FK nullable (-1 si no aplica)
    private String fechaCreacion;

    public Solicitud(int idSolicitud, String tipo, int estado,
                     String datosSolicitud, int idPerfil, String fechaCreacion) {
        this.idSolicitud = idSolicitud;
        this.tipo = tipo;
        this.estado = estado;
        this.datosSolicitud = datosSolicitud;
        this.idPerfil = idPerfil;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public String getTipo() { return tipo; }
    public int getEstado() { return estado; }
    public String getDatosSolicitud() { return datosSolicitud; }
    public int getIdPerfil() { return idPerfil; }
    public String getFechaCreacion() { return fechaCreacion; }

    public void setEstado(int estado) { this.estado = estado; }
    public void setDatosSolicitud(String datosSolicitud) { this.datosSolicitud = datosSolicitud; }

    public boolean isPendiente() { return estado == 0; }
    public boolean isAprobada() { return estado == 1; }
    public boolean isRechazada() { return estado == 2; }

    @Override
    public String toString() {
        String estadoStr = estado == 0 ? "PENDIENTE" : estado == 1 ? "APROBADA" : "RECHAZADA";
        return "Solicitud{idSolicitud=" + idSolicitud + ", tipo=" + tipo
                + ", estado=" + estadoStr + "}";
    }
}
