package com.mycompany.bancoadn.cliente.httpapi.dto;

import com.mycompany.bancoadn.cliente.ClasesModelo.PerfilGenetico;

/**
 * Data Transfer Object for PerfilGenetico.
 */
public class PerfilDto {
    private int idPerfil;
    private String nombreCompleto;
    private String codigoSecuencia;
    private String descripcion;
    private String fechaMuestra; // expected format: yyyy-MM-dd
    private int estado; // 1 = ACTIVO, 0 = INACTIVO
    // For admin actions, we need to know who is performing the action
    private String adminEmail; // email of the admin performing the action (not part of the perfil model)

    // Constructors
    public PerfilDto() {}

    public PerfilDto(int idPerfil, String nombreCompleto, String codigoSecuencia, String descripcion, String fechaMuestra, int estado) {
        this.idPerfil = idPerfil;
        this.nombreCompleto = nombreCompleto;
        this.codigoSecuencia = codigoSecuencia;
        this.descripcion = descripcion;
        this.fechaMuestra = fechaMuestra;
        this.estado = estado;
    }

    // Getters and Setters
    public int getIdPerfil() {
        return idPerfil;
    }

    public void setIdPerfil(int idPerfil) {
        this.idPerfil = idPerfil;
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

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     * Convert from PerfilGenetico model to DTO.
     * @param model the model object
     * @return the DTO
     */
    public static PerfilDto fromModel(PerfilGenetico model) {
        if (model == null) {
            return null;
        }
        return new PerfilDto(
                model.getIdPerfil(),
                model.getNombreCompleto(),
                model.getCodigoSecuencia(),
                model.getDescripcion(),
                model.getFechaMuestra(),
                model.getEstado()
        );
    }

    /**
     * Convert DTO to model object (without adminEmail).
     * @return the model object
     */
    public PerfilGenetico toModel() {
        return new PerfilGenetico(
                this.idPerfil,
                this.nombreCompleto,
                this.codigoSecuencia,
                this.descripcion,
                this.estado,
                this.fechaMuestra,
                -1 // idCuenta - we don't have it in DTO, but the model might not need it for update operations
        );
    }
}