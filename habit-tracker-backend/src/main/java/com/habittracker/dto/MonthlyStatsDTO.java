package com.habittracker.dto;

/**
 * DTO que representa el porcentaje de cumplimiento de un mes.
 * El porcentaje indica que proporcion del total de registros posibles
 * (habitos activos * dias del mes) fueron completados.
 */
public class MonthlyStatsDTO {

    private double percentage;
    private int completedDays;
    private int totalDaysInMonth;

    public MonthlyStatsDTO() {}

    /**
     * Crea un DTO de estadistica mensual con todos los campos.
     *
     * @param percentage      Porcentaje de cumplimiento (0.0 a 100.0)
     * @param completedDays   Total de registros de habitos completados en el mes
     * @param totalDaysInMonth Total de dias del mes
     */
    public MonthlyStatsDTO(double percentage, int completedDays, int totalDaysInMonth) {
        this.percentage = percentage;
        this.completedDays = completedDays;
        this.totalDaysInMonth = totalDaysInMonth;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public int getCompletedDays() {
        return completedDays;
    }

    public void setCompletedDays(int completedDays) {
        this.completedDays = completedDays;
    }

    public int getTotalDaysInMonth() {
        return totalDaysInMonth;
    }

    public void setTotalDaysInMonth(int totalDaysInMonth) {
        this.totalDaysInMonth = totalDaysInMonth;
    }
}
