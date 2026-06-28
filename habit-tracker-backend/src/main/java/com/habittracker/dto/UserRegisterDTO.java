package com.habittracker.dto;

/**
 * DTO de entrada para el registro de un nuevo usuario.
 * Contiene los datos que envia el frontend en el formulario de registro.
 */
public class UserRegisterDTO {

    private String name;
    private String email;
    private String password;

    public UserRegisterDTO() {}

    /**
     * Nombre completo del usuario.
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Email del usuario (debe ser unico en el sistema).
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Contrasena en texto plano. Se encriptara con BCrypt en el servicio.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
