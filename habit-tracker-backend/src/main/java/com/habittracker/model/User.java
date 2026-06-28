package com.habittracker.model;

import java.sql.Timestamp;

/**
 * Representa un usuario de la aplicacion.
 * Se corresponde con la tabla 'users' de la base de datos.
 */
public class User {

    private int id;
    private String name;
    private String email;
    private String passwordHash;
    private String avatarEmoji;
    private Timestamp createdAt;

    public User() {}

    /**
     * Crea un nuevo usuario con todos los campos.
     *
     * @param id           Identificador unico del usuario
     * @param name         Nombre completo del usuario
     * @param email        Email del usuario
     * @param passwordHash Hash de la contrasena
     * @param avatarEmoji  Emoji del avatar
     * @param createdAt    Fecha de creacion
     */
    public User(int id, String name, String email, String passwordHash,
                String avatarEmoji, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.avatarEmoji = avatarEmoji;
        this.createdAt = createdAt;
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

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAvatarEmoji() {
        return avatarEmoji;
    }

    public void setAvatarEmoji(String avatarEmoji) {
        this.avatarEmoji = avatarEmoji;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
