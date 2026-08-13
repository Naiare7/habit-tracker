package com.habittracker.service;

import com.habittracker.dao.AchievementDAO;
import com.habittracker.dao.HabitDAO;
import com.habittracker.dao.StatsDAO;
import com.habittracker.model.Achievement;
import com.habittracker.model.Habit;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Servicio con la logica de negocio para los logros de la aplicacion.
 * Evalua las condiciones de cada logro tras marcar un habito y desbloquea
 * los que el usuario haya cumplido.
 */
public class AchievementService {

    // Lista de sesion con los logros recien desbloqueados, para que el
    // endpoint /api/achievements/new de la tarea 7.4 pueda consultarlos
    private static final List<Achievement> LOGROS_NUEVOS_SESION = new ArrayList<>();

    private final AchievementDAO achievementDAO;
    private final StatsDAO statsDAO;
    private final HabitDAO habitDAO;

    public AchievementService() {
        this.achievementDAO = new AchievementDAO();
        this.statsDAO = new StatsDAO();
        this.habitDAO = new HabitDAO();
    }

    /**
     * Devuelve los logros recien desbloqueados en esta sesion y vacia la lista.
     * Lo usa el endpoint GET /api/achievements/new.
     *
     * @return Lista de logros desbloqueados desde la ultima consulta
     */
    public static List<Achievement> getNewlyUnlocked() {
        List<Achievement> logros = new ArrayList<>(LOGROS_NUEVOS_SESION);
        LOGROS_NUEVOS_SESION.clear();
        return logros;
    }

    /**
     * Devuelve todos los logros disponibles con su estado de desbloqueo.
     * Combina la lista completa de logros con la lista de logros desbloqueados
     * del usuario para marcar como unlocked (true) los que ya tenga,
     * copiando tambien la fecha de desbloqueo.
     *
     * @param userId ID del usuario
     * @return Lista de todos los logros con su estado de desbloqueo
     */
    public List<Achievement> getAllWithStatus(int userId) {
        List<Achievement> todosLosLogros = achievementDAO.findAll();
        List<Achievement> logrosDesbloqueados = achievementDAO.findUnlockedByUserId(userId);

        // Marca como desbloqueado cada logro que el usuario ya tenga
        // y copia su fecha de desbloqueo desde la lista de desbloqueados
        for (Achievement logro : todosLosLogros) {
            for (Achievement desbloqueado : logrosDesbloqueados) {
                if (desbloqueado.getId() == logro.getId()) {
                    logro.setUnlocked(true);
                    logro.setUnlockedAt(desbloqueado.getUnlockedAt());
                    break;
                }
            }
        }

        return todosLosLogros;
    }

    /**
     * Evalua todos los logros del usuario y desbloquea los que haya cumplido.
     * Los logros ya desbloqueados se ignoran para no repetirlos.
     *
     * @param userId ID del usuario
     * @return Lista con los logros recien desbloqueados en esta llamada
     */
    public List<Achievement> checkAndUnlockAchievements(int userId) {
        List<Achievement> logrosNuevos = new ArrayList<>();

        // Obtiene los logros disponibles y los que el usuario ya tiene
        List<Achievement> todosLosLogros = achievementDAO.findAll();
        List<Achievement> logrosDesbloqueados = achievementDAO.findUnlockedByUserId(userId);

        // Guarda en un Set los ids de logros ya desbloqueados para consultas rapidas
        Set<Integer> idsDesbloqueados = new HashSet<>();
        for (Achievement logro : logrosDesbloqueados) {
            idsDesbloqueados.add(logro.getId());
        }

        // Calcula las estadisticas del usuario una sola vez
        int rachaMaxima = calcularRachaMaxima(userId);
        boolean semanaPerfecta = esSemanaPerfecta(userId);
        boolean mesPerfecto = esMesPerfecto(userId);

        // Compara cada logro con las estadisticas del usuario
        for (Achievement logro : todosLosLogros) {
            if (idsDesbloqueados.contains(logro.getId())) {
                continue;
            }

            if (cumpleCondicion(logro, rachaMaxima, semanaPerfecta, mesPerfecto)) {
                boolean guardado = achievementDAO.saveUserAchievement(userId, logro.getId());

                if (guardado) {
                    logro.setUnlocked(true);
                    logro.setUnlockedAt(new Timestamp(System.currentTimeMillis()));
                    logrosNuevos.add(logro);
                    LOGROS_NUEVOS_SESION.add(logro);
                }
            }
        }

        return logrosNuevos;
    }

    /**
     * Comprueba si un logro cumple su condicion con las estadisticas del usuario.
     *
     * @param logro          El logro a evaluar
     * @param rachaMaxima    Racha mas larga del usuario en cualquier habito
     * @param semanaPerfecta true si el usuario ha completado una semana perfecta
     * @param mesPerfecto    true si el usuario ha completado un mes perfecto
     * @return true si el usuario cumple la condicion del logro
     */
    private boolean cumpleCondicion(Achievement logro, int rachaMaxima,
                                    boolean semanaPerfecta, boolean mesPerfecto) {
        switch (logro.getConditionType()) {
            case "streak_days":
                return rachaMaxima >= logro.getConditionValue();
            case "perfect_week":
                return semanaPerfecta;
            case "perfect_month":
                return mesPerfecto;
            default:
                return false;
        }
    }

    /**
     * Calcula la racha mas larga que tiene el usuario en cualquiera de sus habitos.
     *
     * @param userId ID del usuario
     * @return Numero de dias consecutivos de la racha mas larga
     */
    private int calcularRachaMaxima(int userId) {
        int rachaMaxima = 0;
        List<Habit> habitos = habitDAO.findByUserId(userId);

        for (Habit habito : habitos) {
            int racha = statsDAO.getStreak(habito.getId());
            if (racha > rachaMaxima) {
                rachaMaxima = racha;
            }
        }

        return rachaMaxima;
    }

    /**
     * Comprueba si el usuario ha completado todos sus habitos activos
     * durante los ultimos 7 dias (semana perfecta).
     *
     * @param userId ID del usuario
     * @return true si los ultimos 7 dias estan completamente cumplidos
     */
    private boolean esSemanaPerfecta(int userId) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicio = hoy.minusDays(6);
        return todosLosDiasCompletos(userId, inicio, hoy);
    }

    /**
     * Comprueba si el usuario ha completado todos sus habitos activos
     * todos los dias del mes actual hasta hoy (mes perfecto).
     *
     * @param userId ID del usuario
     * @return true si todos los dias del mes (hasta hoy) estan cumplidos
     */
    private boolean esMesPerfecto(int userId) {
        LocalDate hoy = LocalDate.now();
        LocalDate primerDiaMes = hoy.withDayOfMonth(1);
        return todosLosDiasCompletos(userId, primerDiaMes, hoy);
    }

    /**
     * Comprueba que todos los dias del rango tengan todos los habitos completados.
     * Se considera que un dia esta perfecto cuando el numero de habitos completados
     * ese dia es igual al total de habitos activos del usuario.
     *
     * @param userId ID del usuario
     * @param desde  Primer dia del rango (inclusive)
     * @param hasta  Ultimo dia del rango (inclusive)
     * @return true si todos los dias del rango estan completamente cumplidos
     */
    private boolean todosLosDiasCompletos(int userId, LocalDate desde, LocalDate hasta) {
        int totalHabitosActivos = statsDAO.getTotalActiveHabits(userId);

        if (totalHabitosActivos == 0) {
            return false;
        }

        Map<LocalDate, Integer> completadosPorDia =
            statsDAO.getDailyCompletedCount(userId, desde, hasta);

        LocalDate diaActual = desde;
        while (!diaActual.isAfter(hasta)) {
            int completados = completadosPorDia.getOrDefault(diaActual, 0);
            if (completados < totalHabitosActivos) {
                return false;
            }
            diaActual = diaActual.plusDays(1);
        }

        return true;
    }
}
