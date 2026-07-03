package com.habittracker.dto;

/**
 * DTO que representa un dia de la semana con su conteo de habitos completados.
 * Se usa para devolver las estadisticas semanales al frontend.
 */
public class WeeklyStatsEntryDTO {

    private String dayName;
    private int completedCount;
    private String date;

    public WeeklyStatsEntryDTO() {}

    /**
     * Crea una entrada de estadistica semanal con todos los campos.
     *
     * @param dayName        Nombre del dia (ej: "Lunes", "Martes")
     * @param completedCount Numero de habitos completados ese dia
     * @param date           Fecha en formato ISO (YYYY-MM-DD)
     */
    public WeeklyStatsEntryDTO(String dayName, int completedCount, String date) {
        this.dayName = dayName;
        this.completedCount = completedCount;
        this.date = date;
    }

    public String getDayName() {
        return dayName;
    }

    public void setDayName(String dayName) {
        this.dayName = dayName;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
