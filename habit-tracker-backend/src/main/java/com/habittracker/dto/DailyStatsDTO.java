package com.habittracker.dto;

/**
 * DTO que representa el nivel de cumplimiento de un dia del mes.
 * Se usa para devolver al frontend los datos del calendario de habitos.
 */
public class DailyStatsDTO {

    private String date;
    private int completedCount;
    private int totalHabits;
    private double percentage;

    public DailyStatsDTO() {}

    /**
     * Crea un DTO de estadistica diaria con todos los campos.
     *
     * @param date           Fecha del dia en formato ISO (YYYY-MM-DD)
     * @param completedCount Numero de habitos completados ese dia
     * @param totalHabits    Total de habitos activos del usuario
     * @param percentage     Porcentaje de cumplimiento del dia (0.0 a 100.0)
     */
    public DailyStatsDTO(String date, int completedCount, int totalHabits, double percentage) {
        this.date = date;
        this.completedCount = completedCount;
        this.totalHabits = totalHabits;
        this.percentage = percentage;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public void setCompletedCount(int completedCount) {
        this.completedCount = completedCount;
    }

    public int getTotalHabits() {
        return totalHabits;
    }

    public void setTotalHabits(int totalHabits) {
        this.totalHabits = totalHabits;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
