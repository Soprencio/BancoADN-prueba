package com.mycompany.bancoadn.cliente.httpapi.dto;

public class AuthDto {

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private String email;
        private String nombreCuenta;
        private int idRol;
        private int idCuenta; // will be -1 for login, set later if known

        public LoginResponse(String email, String nombreCuenta, int idRol) {
            this.email = email;
            this.nombreCuenta = nombreCuenta;
            this.idRol = idRol;
            this.idCuenta = -1; // not available from login
        }

        public String getEmail() {
            return email;
        }

        public String getNombreCuenta() {
            return nombreCuenta;
        }

        public int getIdRol() {
            return idRol;
        }

        public int getIdCuenta() {
            return idCuenta;
        }

        // Setter for idCuenta if needed later (not used in login)
        public void setIdCuenta(int idCuenta) {
            this.idCuenta = idCuenta;
        }
    }

    public static class CreateAccountRequest {
        private String email;
        private String nombreCuenta;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getNombreCuenta() {
            return nombreCuenta;
        }

        public void setNombreCuenta(String nombreCuenta) {
            this.nombreCuenta = nombreCuenta;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class CreateAccountResponse {
        private boolean success;
        private String message;

        public CreateAccountResponse(boolean success, String message) {
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

    // Inner class for result from service
    public static class CreateAccountResult {
        private boolean success;
        private String message;

        public CreateAccountResult(boolean success, String message) {
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
}
