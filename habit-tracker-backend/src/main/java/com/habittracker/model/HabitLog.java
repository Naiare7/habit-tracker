package com.habittracker.model;

import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Representa el registro diario de un habito (completado o no).
 * Se corresponde con la tabla 'habit_logs' de la base de datos.
 */
public class HabitLog {

    private int id;
    private int habitId;
    private int userId;
    private LocalDate completedDate;
    private boolean completed;
    private String notes;
    private Timestamp createdAt;

    public HabitLog() {}

    /**
     * Crea un nuevo registro de habito con todos los campos.
     *
     * @param id            Identificador unico del registro
     * @param habitId       ID del habito asociado
     * @param userId        ID del usuario propietario
     * @param completedDate Fecha del registro
     * @param completed     Indica si fue completado
     * @param notes         Notas opcionales
     * @param createdAt     Fecha de creacion del registro
     */
    public HabitLog(int id, int habitId, int userId, LocalDate completedDate,
                    boolean completed, String notes, Timestamp createdAt) {
        this.id = id;
        this.habitId = habitId;
        this.userId = userId;
        this.completedDate = completedDate;
        this.completed = completed;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
