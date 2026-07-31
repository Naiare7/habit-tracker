package com.habittracker.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.habittracker.dao.StatsDAO;
import com.habittracker.dto.DailyStatsDTO;
import com.habittracker.dto.MonthlyStatsDTO;
import com.habittracker.dto.StreakDTO;
import com.habittracker.dto.WeeklyStatsEntryDTO;

/**
 * Servicio con la logica de negocio para las estadisticas de la aplicacion.
 * Calcula datos semanales, mensuales y rachas usando StatsDAO
 * y los convierte en DTOs para el frontend.
 */
public class StatsService {

    private final StatsDAO statsDAO;

    public StatsService() {
        this.statsDAO = new StatsDAO();
    }

    /**
     * Obtiene las estadisticas de la semana actual para un usuario.
     * Devuelve una lista de 7 elementos (lunes a domingo) indicando
     * cuantos habitos se completaron cada dia.
     *
     * @param userId ID del usuario
     * @return Lista de WeeklyStatsEntryDTO con los datos de cada dia de la semana
     */
    public List<WeeklyStatsEntryDTO> getWeeklyStats(int userId) {
        // Calcula el lunes y domingo de la semana actual
        LocalDate hoy = LocalDate.now();
        LocalDate lunes = hoy.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate domingo = hoy.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Obtiene los datos agrupados por fecha
        Map<LocalDate, Integer> datosPorDia = statsDAO.getWeeklyStats(userId, lunes, domingo);

        // Construye la lista completa de lunes a domingo
        List<WeeklyStatsEntryDTO> resultado = new ArrayList<>();
        LocalDate diaActual = lunes;

        while (!diaActual.isAfter(domingo)) {
            int cantidad = datosPorDia.getOrDefault(diaActual, 0);
            String nombreDia = obtenerNombreDia(diaActual.getDayOfWeek());
            resultado.add(new WeeklyStatsEntryDTO(nombreDia, cantidad, diaActual.toString()));
            diaActual = diaActual.plusDays(1);
        }

        return resultado;
    }

    /**
     * Calcula el porcentaje de cumplimiento de un mes para un usuario.
     * El porcentaje se calcula como: (total completados del mes) /
     * (total habitos activos * dias del mes) * 100.
     *
     * @param userId ID del usuario
     * @param year   Anho del mes a consultar
     * @param month  Mes a consultar (1 = enero, 12 = diciembre)
     * @return MonthlyStatsDTO con el porcentaje y los datos usados para calcularlo
     */
    public MonthlyStatsDTO getMonthlyStats(int userId, int year, int month) {
        LocalDate primerDia = LocalDate.of(year, month, 1);
        LocalDate ultimoDia = primerDia.with(TemporalAdjusters.lastDayOfMonth());
        int totalDiasMes = ultimoDia.getDayOfMonth();

        int totalHabitosActivos = statsDAO.getTotalActiveHabits(userId);

        if (totalHabitosActivos == 0) {
            return new MonthlyStatsDTO(0.0, 0, totalDiasMes);
        }

        int totalCompletados = statsDAO.getMonthlyTotalCompleted(userId, primerDia, ultimoDia);
        int posiblesTotales = totalHabitosActivos * totalDiasMes;

        double porcentaje = ((double) totalCompletados / posiblesTotales) * 100.0;
        porcentaje = Math.round(porcentaje * 100.0) / 100.0;

        return new MonthlyStatsDTO(porcentaje, totalCompletados, totalDiasMes);
    }

    /**
     * Obtiene la racha de un habito: cuantos dias consecutivos se ha completado.
     *
     * @param habitId ID del habito
     * @return StreakDTO con el id del habito y el numero de dias consecutivos
     */
    public StreakDTO getStreak(int habitId) {
        int racha = statsDAO.getStreak(habitId);
        return new StreakDTO(habitId, racha);
    }

    /**
     * Obtiene el nivel de cumplimiento de cada dia del mes para el calendario.
     * Solo devuelve los dias que tienen registros: los dias sin ningun registro
     * se omiten para que el frontend pueda distinguirlos como "sin datos".
     *
     * @param userId ID del usuario
     * @param year   Anho del mes a consultar
     * @param month  Mes a consultar (1 = enero, 12 = diciembre)
     * @return Lista de DailyStatsDTO con el cumplimiento de cada dia con registros
     */
    public List<DailyStatsDTO> getDailyStats(int userId, int year, int month) {
        LocalDate primerDia = LocalDate.of(year, month, 1);
        LocalDate ultimoDia = primerDia.with(TemporalAdjusters.lastDayOfMonth());

        int totalHabitosActivos = statsDAO.getTotalActiveHabits(userId);
        Map<LocalDate, Integer> completadosPorDia =
            statsDAO.getDailyCompletedCount(userId, primerDia, ultimoDia);

        // Recorre cada dia del mes y crea un DTO solo si tiene registros
        List<DailyStatsDTO> resultado = new ArrayList<>();
        LocalDate diaActual = primerDia;

        while (!diaActual.isAfter(ultimoDia)) {
            if (completadosPorDia.containsKey(diaActual)) {
                int completados = completadosPorDia.get(diaActual);

                double porcentaje = 0.0;
                if (totalHabitosActivos > 0) {
                    porcentaje = ((double) completados / totalHabitosActivos) * 100.0;
                    porcentaje = Math.round(porcentaje * 100.0) / 100.0;
                }

                resultado.add(new DailyStatsDTO(
                    diaActual.toString(), completados, totalHabitosActivos, porcentaje));
            }

            diaActual = diaActual.plusDays(1);
        }

        return resultado;
    }

    /**
     * Convierte un DayOfWeek de Java a su nombre en espanhol.
     *
     * @param dia El enum DayOfWeek (MONDAY, TUESDAY, etc.)
     * @return El nombre del dia en espanhol
     */
    private String obtenerNombreDia(DayOfWeek dia) {
        return switch (dia) {
            case MONDAY    -> "Lunes";
            case TUESDAY   -> "Martes";
            case WEDNESDAY -> "Miercoles";
            case THURSDAY  -> "Jueves";
            case FRIDAY    -> "Viernes";
            case SATURDAY  -> "Sabado";
            case SUNDAY    -> "Domingo";
        };
    }
}
