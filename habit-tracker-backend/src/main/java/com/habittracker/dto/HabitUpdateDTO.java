package com.habittracker.dto;

/**
 * DTO de entrada para actualizar un habito existente.
 * Contiene los mismos campos que HabitCreateDTO mas el id del habito a modificar.
 */
public class HabitUpdateDTO {

    private int id;
    private String name;
    private String description;
    private String emoji;
    private String frequency;
    private int[] targetDays;
    private String color;

    public HabitUpdateDTO() {}

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
}
