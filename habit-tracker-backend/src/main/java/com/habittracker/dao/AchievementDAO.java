package com.habittracker.dao;

import com.habittracker.model.Achievement;
import com.habittracker.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para las tablas 'achievements' y 'user_achievements'.
 * Proporciona metodos para consultar los logros y guardar los desbloqueados.
 */
public class AchievementDAO {

    /**
     * Obtiene todos los logros disponibles en la aplicacion.
     *
     * @return Lista con todos los logros, ordenados por id
     */
    public List<Achievement> findAll() {
        List<Achievement> logros = new ArrayList<>();
        String sql = "SELECT * FROM achievements ORDER BY id ASC";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                logros.add(mapearAchievement(resultado));
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar todos los logros: " + e.getMessage());
        }

        return logros;
    }

    /**
     * Obtiene los logros que ya ha desbloqueado un usuario.
     *
     * @param userId ID del usuario
     * @return Lista de logros desbloqueados, ordenados por fecha de desbloqueo
     */
    public List<Achievement> findUnlockedByUserId(int userId) {
        List<Achievement> logros = new ArrayList<>();
        String sql = "SELECT a.id, a.code, a.name, a.description, a.emoji, "
                   + "a.condition_type, a.condition_value, a.created_at "
                   + "FROM achievements a "
                   + "INNER JOIN user_achievements ua ON a.id = ua.achievement_id "
                   + "WHERE ua.user_id = ? "
                   + "ORDER BY ua.unlocked_at ASC";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    logros.add(mapearAchievement(resultado));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar logros desbloqueados por usuario: " + e.getMessage());
        }

        return logros;
    }

    /**
     * Guarda un logro desbloqueado para un usuario.
     * Si el logro ya estaba desbloqueado no se duplica gracias al ON CONFLICT.
     *
     * @param userId        ID del usuario
     * @param achievementId ID del logro desbloqueado
     */
    public void saveUserAchievement(int userId, int achievementId) {
        String sql = "INSERT INTO user_achievements (user_id, achievement_id) "
                   + "VALUES (?, ?) ON CONFLICT DO NOTHING";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);
            sentencia.setInt(2, achievementId);
            sentencia.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al guardar logro desbloqueado: " + e.getMessage());
        }
    }

    /**
     * Convierte una fila del ResultSet en un objeto Achievement.
     *
     * @param resultado El ResultSet apuntando a la fila actual
     * @return Un objeto Achievement con los datos de la fila
     * @throws SQLException Si hay un error al leer el ResultSet
     */
    private Achievement mapearAchievement(ResultSet resultado) throws SQLException {
        Achievement logro = new Achievement();
        logro.setId(resultado.getInt("id"));
        logro.setCode(resultado.getString("code"));
        logro.setName(resultado.getString("name"));
        logro.setDescription(resultado.getString("description"));
        logro.setEmoji(resultado.getString("emoji"));
        logro.setConditionType(resultado.getString("condition_type"));
        logro.setConditionValue(resultado.getInt("condition_value"));
        logro.setCreatedAt(resultado.getTimestamp("created_at"));
        return logro;
    }
}
