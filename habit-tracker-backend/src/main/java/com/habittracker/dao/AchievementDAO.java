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
 * Data Access Object para la tabla 'achievements'.
 * Proporciona metodos para obtener los logros disponibles y gestionar
 * los logros desbloqueados por cada usuario en 'user_achievements'.
 */
public class AchievementDAO {

    /**
     * Obtiene todos los logros disponibles en la aplicacion.
     * Se devuelven sin estado de desbloqueo (unlocked = false).
     *
     * @return Lista con todos los logros existentes
     */
    public List<Achievement> findAll() {
        List<Achievement> logros = new ArrayList<>();
        String sql = "SELECT * FROM achievements ORDER BY id";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                logros.add(mapearAchievement(resultado, false, null));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todos los logros: " + e.getMessage());
        }

        return logros;
    }

    /**
     * Obtiene los logros que un usuario ya ha desbloqueado.
     * Hace JOIN con 'user_achievements' para saber que logros y cuando se desbloquearon.
     *
     * @param userId ID del usuario
     * @return Lista de logros desbloqueados por el usuario (unlocked = true, con unlockedAt)
     */
    public List<Achievement> findUnlockedByUserId(int userId) {
        List<Achievement> logros = new ArrayList<>();
        String sql = "SELECT a.id, a.code, a.name, a.description, a.emoji, "
                   + "a.condition_type, a.condition_value, ua.unlocked_at "
                   + "FROM achievements a "
                   + "INNER JOIN user_achievements ua ON ua.achievement_id = a.id "
                   + "WHERE ua.user_id = ? "
                   + "ORDER BY ua.unlocked_at";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);

            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    logros.add(mapearAchievement(resultado, true, resultado.getTimestamp("unlocked_at")));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener logros desbloqueados del usuario: " + e.getMessage());
        }

        return logros;
    }

    /**
     * Guarda un logro desbloqueado por un usuario en 'user_achievements'.
     * Usa ON CONFLICT DO NOTHING para no fallar si el logro ya estaba desbloqueado.
     *
     * @param userId        ID del usuario que desbloquea el logro
     * @param achievementId ID del logro desbloqueado
     * @return true si se inserto un nuevo registro, false si ya existia o fallo
     */
    public boolean saveUserAchievement(int userId, int achievementId) {
        String sql = "INSERT INTO user_achievements (user_id, achievement_id) "
                   + "VALUES (?, ?) "
                   + "ON CONFLICT (user_id, achievement_id) DO NOTHING";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, userId);
            sentencia.setInt(2, achievementId);

            return sentencia.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar logro desbloqueado: " + e.getMessage());
        }

        return false;
    }

    /**
     * Convierte una fila del ResultSet en un objeto Achievement.
     *
     * @param resultado  Fila de la consulta SQL
     * @param unlocked   Indica si el logro esta desbloqueado (viene del JOIN)
     * @param unlockedAt Fecha de desbloqueo (null si no viene del JOIN)
     * @return Objeto Achievement con los datos de la fila
     */
    private Achievement mapearAchievement(ResultSet resultado, boolean unlocked,
                                          java.sql.Timestamp unlockedAt) throws SQLException {
        Achievement logro = new Achievement(
            resultado.getInt("id"),
            resultado.getString("code"),
            resultado.getString("name"),
            resultado.getString("description"),
            resultado.getString("emoji"),
            resultado.getString("condition_type"),
            resultado.getInt("condition_value")
        );
        logro.setUnlocked(unlocked);
        logro.setUnlockedAt(unlockedAt);
        return logro;
    }
}
