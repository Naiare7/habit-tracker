package com.habittracker.model;

import java.sql.Timestamp;
import java.time.LocalTime;


/**
 * Representa un recordatorio de un habito.
 * Se corresponde con la tabla 'reminders' de la base de datos.
 */
public class Reminder {

    private int id;
    private int habitId;
    private int userId;
    private LocalTime reminderTime;
    private int[] daysOfWeek;
    private boolean isActive;
    private Timestamp createdAt;

    public Reminder() {}

    /**
     * Crea un nuevo recordatorio con todos los campos.
     *
     * @param id           Identificador unico del recordatorio
     * @param habitId      ID del habito asociado
     * @param userId       ID del usuario propietario
     * @param reminderTime Hora del recordatorio
     * @param daysOfWeek   Dias de la semana en que suena (0=domingo, ..., 6=sabado)
     * @param isActive     Indica si el recordatorio esta activo
     * @param createdAt    Fecha de creacion del recordatorio
     */
    public Reminder(int id, int habitId, int userId, LocalTime reminderTime,
                    int[] daysOfWeek, boolean isActive, Timestamp createdAt) {
        this.id = id;
        this.habitId = habitId;
        this.userId = userId;
        this.reminderTime = reminderTime;
        this.daysOfWeek = daysOfWeek;
        this.isActive = isActive;
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

    public LocalTime getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(LocalTime reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int[] getDaysOfWeek() {
        return daysOfWeek;
    }

    public void setDaysOfWeek(int[] daysOfWeek) {
        this.daysOfWeek = daysOfWeek;
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
