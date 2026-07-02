package com.mycompany.bancoadn.cliente.httpapi.dto;

import java.util.Date;

public class SolicitudDto {

    public static class RegistrarRequest {
        private String nombreCompleto;
        private String codigoSecuencia;
        private String descripcion;
        private String fechaMuestra; // format: yyyy-MM-dd
        private String email;

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public void setNombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
        }

        public String getCodigoSecuencia() {
            return codigoSecuencia;
        }

        public void setCodigoSecuencia(String codigoSecuencia) {
            this.codigoSecuencia = codigoSecuencia;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getFechaMuestra() {
            return fechaMuestra;
        }

        public void setFechaMuestra(String fechaMuestra) {
            this.fechaMuestra = fechaMuestra;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public static class RegistrarResponse {
        private boolean success;
        private String message;

        public RegistrarResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    // Similar for Modificar, Baja, Restaurar requests (they share the same fields as Registrar)
    public static class ModificarRequest extends RegistrarRequest { }
    public static class BajaRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
    public static class RestaurarRequest {
        private String email;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    // Response for Baja and Restaurar is same as RegistrarResponse
    public static class BajaResponse extends RegistrarResponse {
        public BajaResponse(boolean success, String message) {
            super(success, message);
        }
    }
    public static class RestaurarResponse extends RegistrarResponse {
        public RestaurarResponse(boolean success, String message) {
            super(success, message);
        }
    }

    // For listing solicitudes
    public static class SolicitudSummary {
        private int idSolicitud;
        private String tipo; // REGISTRAR, MODIFICAR, BAJA, RESTAURAR
        private int estado; // 0 = PENDIENTE, 1 = APROBADA, 2 = RECHAZADA
        private String email; // email of the user who made the request
        private String fecha; // date of request
        // For REQUEST and MODIFICAR, we might want to include the details
        private String nombreCompleto;
        private String codigoSecuencia;
        private String descripcion;
        private String fechaMuestra;

        // Constructors, getters, setters
        public SolicitudSummary() {}

        public SolicitudSummary(int idSolicitud, String tipo, int estado, String email, String fecha,
                                String nombreCompleto, String codigoSecuencia, String descripcion, String fechaMuestra) {
            this.idSolicitud = idSolicitud;
            this.tipo = tipo;
            this.estado = estado;
            this.email = email;
            this.fecha = fecha;
            this.nombreCompleto = nombreCompleto;
            this.codigoSecuencia = codigoSecuencia;
            this.descripcion = descripcion;
            this.fechaMuestra = fechaMuestra;
        }

        // Getters and setters
        public int getIdSolicitud() {
            return idSolicitud;
        }

        public void setIdSolicitud(int idSolicitud) {
            this.idSolicitud = idSolicitud;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public int getEstado() {
            return estado;
        }

        public void setEstado(int estado) {
            this.estado = estado;
        }

        public String getEstadoString() {
            switch (estado) {
                case 0: return "PENDIENTE";
                case 1: return "APROBADA";
                case 2: return "RECHAZADA";
                default: return "DESCONOCIDO";
            }
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getFecha() {
            return fecha;
        }

        public void setFecha(String fecha) {
            this.fecha = fecha;
        }

        public String getNombreCompleto() {
            return nombreCompleto;
        }

        public void setNombreCompleto(String nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
        }

        public String getCodigoSecuencia() {
            return codigoSecuencia;
        }

        public void setCodigoSecuencia(String codigoSecuencia) {
            this.codigoSecuencia = codigoSecuencia;
        }

        public String getDescripcion() {
            return descripcion;
        }

        public void setDescripcion(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getFechaMuestra() {
            return fechaMuestra;
        }

        public void setFechaMuestra(String fechaMuestra) {
            this.fechaMuestra = fechaMuestra;
        }
    }
}