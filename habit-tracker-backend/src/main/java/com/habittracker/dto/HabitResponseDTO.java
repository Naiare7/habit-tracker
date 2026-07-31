package com.habittracker.dto;

import java.text.SimpleDateFormat;

import com.habittracker.model.Habit;


/**
 * DTO de salida con todos los datos del habito para el frontend.
 * Se construye a partir de un objeto Habit del modelo.
 */
public class HabitResponseDTO {

    private int id;
    private int userId;
    private String name;
    private String description;
    private String emoji;
    private String frequency;
    private int[] targetDays;
    private String color;
    private boolean isActive;
    private String createdAt;

    public HabitResponseDTO() {}

    /**
     * Construye un HabitResponseDTO a partir de un Habit del modelo.
     * Convierte el Timestamp a String con formato ISO.
     */
    public HabitResponseDTO(Habit habito) {
        this.id = habito.getId();
        this.userId = habito.getUserId();
        this.name = habito.getName();
        this.description = habito.getDescription();
        this.emoji = habito.getEmoji();
        this.frequency = habito.getFrequency();
        this.targetDays = habito.getTargetDays();
        this.color = habito.getColor();
        this.isActive = habito.getIsActive();

        if (habito.getCreatedAt() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            this.createdAt = sdf.format(habito.getCreatedAt());
        }
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
