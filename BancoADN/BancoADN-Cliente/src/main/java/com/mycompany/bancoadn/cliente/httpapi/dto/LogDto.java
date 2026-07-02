package com.mycompany.bancoadn.cliente.httpapi.dto;

/**
 * Data Transfer Object for a log entry.
 */
public class LogDto {
    private int idRegistro;
    private String fecha;
    private String nombreCuenta;
    private String email;
    private String descripcion;
    private String acciones;
    private boolean esAdmin;

    // Constructors
    public LogDto() {}

    public LogDto(int idRegistro, String fecha, String nombreCuenta, String email, String descripcion, String acciones, boolean esAdmin) {
        this.idRegistro = idRegistro;
        this.fecha = fecha;
        this.nombreCuenta = nombreCuenta;
        this.email = email;
        this.descripcion = descripcion;
        this.acciones = acciones;
        this.esAdmin = esAdmin;
    }

    // Getters and Setters
    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNombreCuenta() {
        return nombreCuenta;
    }

    public void setNombreCuenta(String nombreCuenta) {
        this.nombreCuenta = nombreCuenta;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getAcciones() {
        return acciones;
    }

    public void setAcciones(String acciones) {
        this.acciones = acciones;
    }

    public boolean isEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(boolean esAdmin) {
        this.esAdmin = esAdmin;
    }

    /**
     * Convert from string representation (as received from server) to DTO.
     * Expected format: "id - unused - nombreCuenta - email - descripcion - fecha - acciones - esAdmin"
     *
     * @param logString the string from the server
     * @return the LogDto or null if parsing fails
     */
    public static LogDto fromString(String logString) {
        if (logString == null || logString.isEmpty()) {
            return null;
        }
        String[] parts = logString.split(" - ", 8); // Split into max 8 parts
        if (parts.length < 8) {
            return null;
        }
        try {
            int id = Integer.parseInt(parts[0].trim());
            // parts[1] is the unused field, we ignore it
            String nombreCuenta = parts[2].trim();
            String email = parts[3].trim();
            String descripcion = parts[4].trim();
            String fecha = parts[5].trim();
            String acciones = parts[6].trim();
            boolean esAdmin = parts[7].trim().equals("1");
            return new LogDto(id, fecha, nombreCuenta, email, descripcion, acciones, esAdmin);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
