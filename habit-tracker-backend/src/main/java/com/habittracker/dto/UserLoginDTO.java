package com.habittracker.dto;

/**
 * DTO de entrada para el inicio de sesion.
 * Contiene las credenciales que envia el frontend.
 */
public class UserLoginDTO {

    private String email;
    private String password;

    public UserLoginDTO() {}

    /**
     * Email del usuario.
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Contrasena en texto plano. Se verificara contra el hash en BD.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
