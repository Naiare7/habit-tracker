package com.habittracker.dao;

import com.habittracker.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object para las estadisticas de la aplicacion.
 * Proporciona consultas SQL que calculan porcentajes de cumplimiento,
 * conteos diarios y rachas de los habitos del usuario.
 */
public class StatsDAO {

    /**
     * Obtiene cuantos habitos se completaron cada dia de la semana para un usuario.
     * Solo incluye los dias del rango que tienen registros completados.
     *
     * @param userId   ID del usuario
     * @param weekStart Fecha de inicio de la semana (lunes)
     * @param weekEnd   Fecha de fin de la semana (domingo)
     * @return Mapa donde la clave es la fecha y el valor es la cantidad de habitos completados ese dia
     */
    public Map<LocalDate, Integer> getWeeklyStats(int userId, LocalDate weekStart, LocalDate weekEnd) {
        Map<LocalDate, Integer> stats = new HashMap<>();
        String sql = "SELECT completed_date, COUNT(*) AS completed_count "
                   + "FROM habit_logs "
                   + "WHERE user_id = ? AND completed = TRUE "
                   + "AND completed_date BETWEEN ? AND ? "
                   + "GROUP BY completed_date "
                   + "ORDER BY completed_date";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);
            sentencia.setDate(2, Date.valueOf(weekStart));
            sentencia.setDate(3, Date.valueOf(weekEnd));

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    LocalDate fecha = resultado.getDate("completed_date").toLocalDate();
                    int cantidad = resultado.getInt("completed_count");
                    stats.put(fecha, cantidad);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener estadisticas semanales: " + e.getMessage());
        }

        return stats;
    }

    /**
     * Cuenta el total de habitos activos que tiene un usuario.
     * Se usa para calcular el porcentaje de cumplimiento mensual.
     *
     * @param userId ID del usuario
     * @return Numero total de habitos activos del usuario
     */
    public int getTotalActiveHabits(int userId) {
        String sql = "SELECT COUNT(*) AS total FROM habits "
                   + "WHERE user_id = ? AND is_active = TRUE";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar habitos activos: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Cuenta cuantos registros de habitos completados hay en un mes para un usuario.
     *
     * @param userId    ID del usuario
     * @param monthStart Primer dia del mes
     * @param monthEnd   Ultimo dia del mes
     * @return Total de registros completados en el mes
     */
    public int getMonthlyTotalCompleted(int userId, LocalDate monthStart, LocalDate monthEnd) {
        String sql = "SELECT COUNT(*) AS total FROM habit_logs "
                   + "WHERE user_id = ? AND completed = TRUE "
                   + "AND completed_date BETWEEN ? AND ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);
            sentencia.setDate(2, Date.valueOf(monthStart));
            sentencia.setDate(3, Date.valueOf(monthEnd));

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al contar completados del mes: " + e.getMessage());
        }

        return 0;
    }

    /**
     * Calcula cuantos dias consecutivos se ha completado un habito hasta la fecha actual.
     * Recorre las fechas desde hoy hacia atras contando los dias en que el habito
     * fue completado, y se detiene en cuanto encuentra un dia sin completar.
     *
     * @param habitId ID del habito
     * @return Numero de dias consecutivos completados (0 si hoy no esta completado)
     */
    public int getStreak(int habitId) {
        String sql = "SELECT completed_date FROM habit_logs "
                   + "WHERE habit_id = ? AND completed = TRUE "
                   + "ORDER BY completed_date DESC";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, habitId);

            try (ResultSet resultado = sentencia.executeQuery()) {
                int racha = 0;
                LocalDate esperado = LocalDate.now();

                while (resultado.next()) {
                    LocalDate fecha = resultado.getDate("completed_date").toLocalDate();

                    if (fecha.isAfter(esperado)) {
                        continue;
                    }

                    if (fecha.equals(esperado)) {
                        racha++;
                        esperado = esperado.minusDays(1);
                    } else {
                        break;
                    }
                }

                return racha;
            }
        } catch (SQLException e) {
            System.err.println("Error al calcular racha del habito: " + e.getMessage());
        }

        return 0;
    }
}
