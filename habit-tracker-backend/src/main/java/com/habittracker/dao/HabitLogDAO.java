package com.habittracker.dao;

import com.habittracker.model.HabitLog;
import com.habittracker.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'habit_logs'.
 * Proporciona metodos para acceder y manipular los registros diarios de habitos.
 */
public class HabitLogDAO {

    /**
     * Busca un registro por id de habito y fecha.
     *
     * @param habitId ID del habito
     * @param date    Fecha del registro
     * @return El objeto HabitLog si existe, null si no se encuentra
     */
    public HabitLog findByHabitIdAndDate(int habitId, LocalDate date) {
        String sql = "SELECT * FROM habit_logs WHERE habit_id = ? AND completed_date = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, habitId);
            sentencia.setDate(2, Date.valueOf(date));

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearHabitLog(resultado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar registro por habito y fecha: " + e.getMessage());
        }

        return null;
    }

    /**
     * Obtiene todos los registros de un usuario en un rango de fechas.
     *
     * @param userId ID del usuario
     * @param from   Fecha de inicio (incluida)
     * @param to     Fecha de fin (incluida)
     * @return Lista de registros del usuario en el rango
     */
    public List<HabitLog> findByUserIdAndDateRange(int userId, LocalDate from, LocalDate to) {
        List<HabitLog> registros = new ArrayList<>();
        String sql = "SELECT * FROM habit_logs WHERE user_id = ? "
                   + "AND completed_date BETWEEN ? AND ? "
                   + "ORDER BY completed_date ASC";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);
            sentencia.setDate(2, Date.valueOf(from));
            sentencia.setDate(3, Date.valueOf(to));

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    registros.add(mapearHabitLog(resultado));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar registros por usuario y rango: " + e.getMessage());
        }

        return registros;
    }

    /**
     * Guarda un registro nuevo o actualiza uno existente.
     * Usa INSERT ... ON CONFLICT para hacer un UPSERT en una sola operacion.
     * Si ya existe un registro para el mismo habito y fecha, lo actualiza.
     * Si no existe, lo crea.
     *
     * @param log El objeto HabitLog con los datos a guardar
     * @return El mismo objeto con el id asignado, o null si falla
     */
    public HabitLog saveOrUpdate(HabitLog log) {
        String sql = "INSERT INTO habit_logs (habit_id, user_id, completed_date, completed, notes) "
                   + "VALUES (?, ?, ?, ?, ?) "
                   + "ON CONFLICT (habit_id, completed_date) "
                   + "DO UPDATE SET completed = EXCLUDED.completed, notes = EXCLUDED.notes";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setInt(1, log.getHabitId());
            sentencia.setInt(2, log.getUserId());
            sentencia.setDate(3, Date.valueOf(log.getCompletedDate()));
            sentencia.setBoolean(4, log.isCompleted());
            sentencia.setString(5, log.getNotes());

            sentencia.executeUpdate();

            try (ResultSet clavesGeneradas = sentencia.getGeneratedKeys()) {
                if (clavesGeneradas.next()) {
                    log.setId(clavesGeneradas.getInt(1));
                }
            }

            log.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            return log;
        } catch (SQLException e) {
            System.err.println("Error al guardar o actualizar registro: " + e.getMessage());
        }

        return null;
    }

    /**
     * Convierte una fila del ResultSet en un objeto HabitLog.
     *
     * @param resultado El ResultSet con los datos de la fila
     * @return Un objeto HabitLog con los valores de la fila
     */
    private HabitLog mapearHabitLog(ResultSet resultado) throws SQLException {
        HabitLog log = new HabitLog();
        log.setId(resultado.getInt("id"));
        log.setHabitId(resultado.getInt("habit_id"));
        log.setUserId(resultado.getInt("user_id"));

        Date fecha = resultado.getDate("completed_date");
        if (fecha != null) {
            log.setCompletedDate(fecha.toLocalDate());
        }

        log.setCompleted(resultado.getBoolean("completed"));
        log.setNotes(resultado.getString("notes"));
        log.setCreatedAt(resultado.getTimestamp("created_at"));
        return log;
    }
}
