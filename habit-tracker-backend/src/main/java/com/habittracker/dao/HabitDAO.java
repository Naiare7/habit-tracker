package com.habittracker.dao;

import com.habittracker.model.Habit;
import com.habittracker.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla 'habits'.
 * Proporciona metodos para acceder y manipular los habitos en la base de datos.
 */
public class HabitDAO {

    /**
     * Obtiene todos los habitos activos de un usuario.
     *
     * @param userId ID del usuario
     * @return Lista de habitos activos del usuario
     */
    public List<Habit> findByUserId(int userId) {
        List<Habit> habitos = new ArrayList<>();
        String sql = "SELECT * FROM habits WHERE user_id = ? AND is_active = TRUE "
                   + "ORDER BY created_at DESC";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    habitos.add(mapearHabit(resultado));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar habitos por usuario: " + e.getMessage());
        }

        return habitos;
    }

    /**
     * Inserta un nuevo habito en la base de datos.
     *
     * @param habito El objeto Habit con los datos a guardar
     * @return El mismo habito con el id asignado por la BD, o null si falla
     */
    public Habit save(Habit habito) {
        String sql = "INSERT INTO habits (user_id, name, description, emoji, "
                   + "frequency, target_days, color) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setInt(1, habito.getUserId());
            sentencia.setString(2, habito.getName());
            sentencia.setString(3, habito.getDescription());
            sentencia.setString(4, habito.getEmoji());
            sentencia.setString(5, habito.getFrequency());

            if (habito.getTargetDays() != null) {
                Array arrayDias = conexion.createArrayOf("integer", toIntegerArray(habito.getTargetDays()));
                sentencia.setArray(6, arrayDias);
            } else {
                sentencia.setNull(6, Types.ARRAY);
            }

            sentencia.setString(7, habito.getColor());

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet clavesGeneradas = sentencia.getGeneratedKeys()) {
                    if (clavesGeneradas.next()) {
                        habito.setId(clavesGeneradas.getInt(1));
                    }
                }
                habito.setIsActive(true);
                habito.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                return habito;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar habito: " + e.getMessage());
        }

        return null;
    }

    /**
     * Actualiza un habito existente.
     *
     * @param habito El objeto Habit con los datos actualizados
     * @return true si se actualizo correctamente, false si no
     */
    public boolean update(Habit habito) {
        String sql = "UPDATE habits SET name = ?, description = ?, emoji = ?, "
                   + "frequency = ?, target_days = ?, color = ? "
                   + "WHERE id = ? AND user_id = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, habito.getName());
            sentencia.setString(2, habito.getDescription());
            sentencia.setString(3, habito.getEmoji());
            sentencia.setString(4, habito.getFrequency());

            if (habito.getTargetDays() != null) {
                Array arrayDias = conexion.createArrayOf("integer", toIntegerArray(habito.getTargetDays()));
                sentencia.setArray(5, arrayDias);
            } else {
                sentencia.setNull(5, Types.ARRAY);
            }

            sentencia.setString(6, habito.getColor());
            sentencia.setInt(7, habito.getId());
            sentencia.setInt(8, habito.getUserId());

            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar habito: " + e.getMessage());
        }

        return false;
    }

    /**
     * Desactiva un habito (borrado logico).
     * Pone is_active = FALSE en lugar de eliminar el registro.
     *
     * @param habitId ID del habito a desactivar
     * @return true si se desactivo correctamente, false si no
     */
    public boolean deactivate(int habitId) {
        String sql = "UPDATE habits SET is_active = FALSE WHERE id = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, habitId);
            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar habito: " + e.getMessage());
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
     * Convierte una fila del ResultSet en un objeto Habit.
     */
    private Habit mapearHabit(ResultSet resultado) throws SQLException {
        Habit habito = new Habit();
        habito.setId(resultado.getInt("id"));
        habito.setUserId(resultado.getInt("user_id"));
        habito.setName(resultado.getString("name"));
        habito.setDescription(resultado.getString("description"));
        habito.setEmoji(resultado.getString("emoji"));
        habito.setFrequency(resultado.getString("frequency"));

        Array arrayDias = resultado.getArray("target_days");
        if (arrayDias != null) {
            habito.setTargetDays((int[]) arrayDias.getArray());
        }

        habito.setColor(resultado.getString("color"));
        habito.setIsActive(resultado.getBoolean("is_active"));
        habito.setCreatedAt(resultado.getTimestamp("created_at"));
        return habito;
    }
}
