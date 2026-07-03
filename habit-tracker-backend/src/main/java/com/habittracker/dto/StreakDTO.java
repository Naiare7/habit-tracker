package com.habittracker.dto;

/**
 * DTO que representa la racha actual de un habito.
 * Indica cuantos dias consecutivos se ha completado el habito hasta hoy.
 */
public class StreakDTO {

    private int habitId;
    private int streak;

    public StreakDTO() {}

    /**
     * Crea un DTO de racha con los campos especificados.
     *
     * @param habitId ID del habito
     * @param streak  Numero de dias consecutivos completados
     */
    public StreakDTO(int habitId, int streak) {
        this.habitId = habitId;
        this.streak = streak;
    }

    public int getHabitId() {
        return habitId;
    }

    public void setHabitId(int habitId) {
        this.habitId = habitId;
    }

    public int getStreak() {
        return streak;
    }

    public void setStreak(int streak) {
        this.streak = streak;
    }
}
