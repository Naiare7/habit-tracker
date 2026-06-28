package com.habittracker.dto;

import com.habittracker.model.User;

/**
 * DTO de salida con los datos del usuario.
 * No incluye la contrasena por seguridad.
 * Se devuelve al frontend tras un registro o inicio de sesion exitoso.
 */
public class UserResponseDTO {

    private int id;
    private String name;
    private String email;
    private String avatarEmoji;
    private String token;

    public UserResponseDTO() {}

    /**
     * Crea un UserResponseDTO a partir de un objeto User.
     * Solo copia los campos seguros (sin contrasena).
     *
     * @param usuario El objeto User del modelo
     */
    public UserResponseDTO(User usuario) {
        this.id = usuario.getId();
        this.name = usuario.getName();
        this.email = usuario.getEmail();
        this.avatarEmoji = usuario.getAvatarEmoji();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public void setAvatarEmoji(String avatarEmoji) {
        this.avatarEmoji = avatarEmoji;
    }

    /**
     * Token de sesion generado al iniciar sesion.
     * Es un UUID que el frontend guarda en localStorage.
     */
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
