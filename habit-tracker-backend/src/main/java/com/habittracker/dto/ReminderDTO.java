package com.habittracker.dto;

import com.habittracker.model.Reminder;

import java.time.format.DateTimeFormatter;

/**
 * DTO de entrada/salida para los recordatorios de habitos.
 * Contiene los datos que se intercambian entre el frontend y el backend.
 * La hora se representa como String con formato HH:mm para que Gson la serialice correctamente.
 */
public class ReminderDTO {

    private int id;
    private int habitId;
    private int userId;
    private String reminderTime;
    private int[] daysOfWeek;
    private boolean isActive;

    public ReminderDTO() {}

    /**
     * Construye un ReminderDTO a partir de un Reminder del modelo.
     * Convierte el LocalTime a String con formato HH:mm.
     *
     * @param recordatorio El recordatorio del modelo
     */
    public ReminderDTO(Reminder recordatorio) {
        this.id = recordatorio.getId();
        this.habitId = recordatorio.getHabitId();
        this.userId = recordatorio.getUserId();
        this.isActive = recordatorio.getIsActive();
        this.daysOfWeek = recordatorio.getDaysOfWeek();

        if (recordatorio.getReminderTime() != null) {
            this.reminderTime = recordatorio.getReminderTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        }
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

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
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
}
