package com.habittracker.model;

import java.sql.Timestamp;
import java.util.Arrays;

/**
 * Representa un habito creado por un usuario.
 * Se corresponde con la tabla 'habits' de la base de datos.
 */
public class Habit {

    private int id;
    private int userId;
    private String name;
    private String description;
    private String emoji;
    private String frequency;
    private int[] targetDays;
    private String color;
    private boolean isActive;
    private Timestamp createdAt;

    public Habit() {}

    /**
     * Crea un nuevo habito con todos los campos.
     */
    public Habit(int id, int userId, String name, String description, String emoji,
                 String frequency, int[] targetDays, String color,
                 boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
        this.frequency = frequency;
        this.targetDays = targetDays;
        this.color = color;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public int[] getTargetDays() {
        return targetDays;
    }

    public void setTargetDays(int[] targetDays) {
        this.targetDays = targetDays;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
