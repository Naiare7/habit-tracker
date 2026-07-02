package com.habittracker.dto;

/**
 * DTO de entrada/salida para los registros diarios de habitos.
 * Contiene los datos que se intercambian entre el frontend y el backend.
 */
public class HabitLogDTO {

    private int habitId;
    private int userId;
    private String date;
    private boolean completed;
    private String notes;

    public HabitLogDTO() {}

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
