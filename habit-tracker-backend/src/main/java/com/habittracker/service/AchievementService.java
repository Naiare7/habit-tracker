package com.habittracker.service;

import com.habittracker.dao.AchievementDAO;
import com.habittracker.dao.HabitDAO;
import com.habittracker.dao.StatsDAO;
import com.habittracker.model.Achievement;
import com.habittracker.model.Habit;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servicio con la logica de negocio para evaluar y desbloquear logros.
 * Comprueba las condiciones de cada logro a partir de las estadisticas
 * del usuario y guarda los que se desbloquean por primera vez.
 */
public class AchievementService {

    private final AchievementDAO achievementDAO;
    private final StatsDAO statsDAO;
    private final HabitDAO habitDAO;

    public AchievementService() {
        this.achievementDAO = new AchievementDAO();
        this.statsDAO = new StatsDAO();
        this.habitDAO = new HabitDAO();
    }

    /**
     * Evalua todos los logros de la aplicacion para un usuario y desbloquea
     * los que cumplan su condicion y aun no estuvieran desbloqueados.
     *
     * @param userId ID del usuario a evaluar
     * @return Lista con los logros desbloqueados en esta evaluacion
     */
    public List<Achievement> checkAndUnlockAchievements(int userId) {
        List<Achievement> todosLosLogros = achievementDAO.findAll();
        List<Achievement> logrosDesbloqueados = achievementDAO.findUnlockedByUserId(userId);

        // Guarda los ids ya desbloqueados para no volver a guardarlos
        Set<Integer> idsDesbloqueados = new HashSet<>();
        for (Achievement logro : logrosDesbloqueados) {
            idsDesbloqueados.add(logro.getId());
        }

        // Calcula los datos necesarios una sola vez para todos los logros
        int rachaMaxima = calcularRachaMaxima(userId);
        boolean primeraSemana = haCompletadoUltimaSemana(userId);
        boolean mesPerfecto = esMesPerfecto(userId);

        List<Achievement> logrosNuevos = new ArrayList<>();

        for (Achievement logro : todosLosLogros) {
            if (idsDesbloqueados.contains(logro.getId())) {
                continue;
            }

            boolean cumple = cumpleCondicion(logro, rachaMaxima, primeraSemana, mesPerfecto);

            if (cumple) {
                achievementDAO.saveUserAchievement(userId, logro.getId());
                logrosNuevos.add(logro);
            }
        }

        return logrosNuevos;
    }

    /**
     * Calcula la racha mas larga entre todos los habitos activos del usuario.
     *
     * @param userId ID del usuario
     * @return La racha maxima encontrada (0 si no tiene habitos o rachas)
     */
    private int calcularRachaMaxima(int userId) {
        List<Habit> habitos = habitDAO.findByUserId(userId);
        int rachaMaxima = 0;

        for (Habit habito : habitos) {
            int racha = statsDAO.getStreak(habito.getId());
            if (racha > rachaMaxima) {
                rachaMaxima = racha;
            }
        }

        return rachaMaxima;
    }

    /**
     * Comprueba si el usuario ha completado al menos un habito cada uno
     * de los ultimos 7 dias, incluido hoy.
     *
     * @param userId ID del usuario
     * @return true si ha tenido actividad los 7 dias, false si no
     */
    private boolean haCompletadoUltimaSemana(int userId) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(6);

        Map<LocalDate, Integer> completadosPorDia =
            statsDAO.getDailyCompletedCount(userId, inicio, hoy);

        // Cuenta cuantos dias de la ventana tienen al menos un habito completado
        int diasConActividad = 0;
        for (int valor : completadosPorDia.values()) {
            if (valor > 0) {
                diasConActividad++;
            }
        }

        return diasConActividad >= 7;
    }

    /**
     * Comprueba si el usuario ha completado todos sus habitos activos
     * todos los dias del mes actual.
     *
     * @param userId ID del usuario
     * @return true si el mes actual esta completo, false si no
     */
    private boolean esMesPerfecto(int userId) {
        int totalHabitosActivos = statsDAO.getTotalActiveHabits(userId);

        if (totalHabitosActivos == 0) {
            return false;
        }

        YearMonth mesActual = YearMonth.now();
        LocalDate primerDia = mesActual.atDay(1);
        LocalDate ultimoDia = mesActual.atEndOfMonth();
        int diasDelMes = mesActual.lengthOfMonth();

        int totalCompletados =
            statsDAO.getMonthlyTotalCompleted(userId, primerDia, ultimoDia);

        int posiblesTotales = totalHabitosActivos * diasDelMes;

        return totalCompletados >= posiblesTotales;
    }

    /**
     * Comprueba si un logro cumple su condicion segun los datos calculados.
     *
     * @param logro         El logro a evaluar
     * @param rachaMaxima   La racha mas larga del usuario
     * @param primeraSemana Si el usuario ha completado la ultima semana
     * @param mesPerfecto   Si el usuario ha completado el mes actual
     * @return true si el logro debe desbloquearse, false si no
     */
    private boolean cumpleCondicion(Achievement logro, int rachaMaxima,
                                    boolean primeraSemana, boolean mesPerfecto) {
        String tipo = logro.getConditionType();

        if ("streak".equals(tipo)) {
            return rachaMaxima >= logro.getConditionValue();
        }

        if ("first_week".equals(tipo)) {
            return primeraSemana;
        }

        if ("perfect_month".equals(tipo)) {
            return mesPerfecto;
        }

        return false;
    }
}
