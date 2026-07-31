package com.habittracker.dao;

import com.habittracker.model.Reminder;
import com.habittracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'reminders'.
 * Proporciona metodos para acceder y manipular los recordatorios en la base de datos.
 */
public class ReminderDAO {

    /**
     * Obtiene todos los recordatorios de un habito.
     *
     * @param habitId ID del habito
     * @return Lista de recordatorios del habito
     */
    public List<Reminder> findByHabitId(int habitId) {
        List<Reminder> recordatorios = new ArrayList<>();
        String sql = "SELECT * FROM reminders WHERE habit_id = ? "
                   + "ORDER BY reminder_time ASC";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, habitId);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    recordatorios.add(mapearReminder(resultado));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar recordatorios por habito: " + e.getMessage());
        }

        return recordatorios;
    }

    /**
     * Inserta un nuevo recordatorio en la base de datos.
     *
     * @param recordatorio El objeto Reminder con los datos a guardar
     * @return El mismo recordatorio con el id asignado por la BD, o null si falla
     */
    public Reminder save(Reminder recordatorio) {
        String sql = "INSERT INTO reminders (habit_id, user_id, reminder_time, days_of_week) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setInt(1, recordatorio.getHabitId());
            sentencia.setInt(2, recordatorio.getUserId());

            if (recordatorio.getReminderTime() != null) {
                sentencia.setTime(3, Time.valueOf(recordatorio.getReminderTime()));
            } else {
                sentencia.setNull(3, Types.TIME);
            }

            if (recordatorio.getDaysOfWeek() != null) {
                Array arrayDias = conexion.createArrayOf("integer", toIntegerArray(recordatorio.getDaysOfWeek()));
                sentencia.setArray(4, arrayDias);
            } else {
                sentencia.setNull(4, Types.ARRAY);
            }

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet clavesGeneradas = sentencia.getGeneratedKeys()) {
                    if (clavesGeneradas.next()) {
                        recordatorio.setId(clavesGeneradas.getInt(1));
                    }
                }
                recordatorio.setIsActive(true);
                recordatorio.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                return recordatorio;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar recordatorio: " + e.getMessage());
        }

        return null;
    }

    /**
     * Actualiza un recordatorio existente.
     *
     * @param recordatorio El objeto Reminder con los datos actualizados
     * @return true si se actualizo correctamente, false si no
     */
    public boolean update(Reminder recordatorio) {
        String sql = "UPDATE reminders SET reminder_time = ?, days_of_week = ?, is_active = ? "
                   + "WHERE id = ? AND user_id = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            if (recordatorio.getReminderTime() != null) {
                sentencia.setTime(1, Time.valueOf(recordatorio.getReminderTime()));
            } else {
                sentencia.setNull(1, Types.TIME);
            }

            if (recordatorio.getDaysOfWeek() != null) {
                Array arrayDias = conexion.createArrayOf("integer", toIntegerArray(recordatorio.getDaysOfWeek()));
                sentencia.setArray(2, arrayDias);
            } else {
                sentencia.setNull(2, Types.ARRAY);
            }

            sentencia.setBoolean(3, recordatorio.getIsActive());
            sentencia.setInt(4, recordatorio.getId());
            sentencia.setInt(5, recordatorio.getUserId());

            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar recordatorio: " + e.getMessage());
        }

        return false;
    }

    /**
     * Elimina un recordatorio de la base de datos.
     *
     * @param reminderId ID del recordatorio a eliminar
     * @return true si se elimino correctamente, false si no
     */
    public boolean delete(int reminderId) {
        String sql = "DELETE FROM reminders WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, reminderId);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al eliminar recordatorio: " + e.getMessage());
        }

        return false;
    }

    /**
     * Convierte un array de int primitivo a Integer[] para PostgreSQL.
     */
    private Integer[] toIntegerArray(int[] array) {
        Integer[] resultado = new Integer[array.length];
        for (int i = 0; i < array.length; i++) {
            resultado[i] = array[i];
        }
        return resultado;
    }

    /**
     * Convierte una fila del ResultSet en un objeto Reminder.
     */
    private Reminder mapearReminder(ResultSet resultado) throws SQLException {
        Reminder recordatorio = new Reminder();
        recordatorio.setId(resultado.getInt("id"));
        recordatorio.setHabitId(resultado.getInt("habit_id"));
        recordatorio.setUserId(resultado.getInt("user_id"));

        Time hora = resultado.getTime("reminder_time");
        if (hora != null) {
            recordatorio.setReminderTime(hora.toLocalTime());
        }

        Array arrayDias = resultado.getArray("days_of_week");
        if (arrayDias != null) {
            recordatorio.setDaysOfWeek((int[]) arrayDias.getArray());
        }

        recordatorio.setIsActive(resultado.getBoolean("is_active"));
        recordatorio.setCreatedAt(resultado.getTimestamp("created_at"));
        return recordatorio;
    }
}
