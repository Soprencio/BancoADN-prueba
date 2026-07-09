package com.mycompany.bancoadn.cliente.ClasesModelo;
 
public class CuentaPersonal {
 
    private int idCuenta;
    private String nombreCuenta;
    private String contrasena;
    private String email;
    private int idRol; // 1=USUARIO, 2=ADMIN
 
    public CuentaPersonal(int idCuenta, String nombreCuenta, String contrasena, String email, int idRol) {
        this.idCuenta = idCuenta;
        this.nombreCuenta = nombreCuenta;
        this.contrasena = contrasena;
        this.email = email;
        this.idRol = idRol;
    }
 
    public int getIdCuenta() { return idCuenta; }
    public String getNombreCuenta() { return nombreCuenta; }
    public String getContrasena() { return contrasena; }
    public String getEmail() { return email; }
    public int getIdRol() { return idRol; }
 
    public void setNombreCuenta(String nombreCuenta) { this.nombreCuenta = nombreCuenta; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
    public void setEmail(String email) { this.email = email; }
    public void setIdRol(int idRol) { this.idRol = idRol; }
 
    @Override
    public String toString() {
        return "CuentaPersonal{idCuenta=" + idCuenta + ", nombreCuenta=" + nombreCuenta
                + ", email=" + email + ", idRol=" + idRol + "}";
    }
}