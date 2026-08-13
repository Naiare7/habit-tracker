package com.habittracker.model;

import java.sql.Timestamp;

/**
 * Representa un logro desbloqueado por un usuario.
 * Se corresponde con la tabla 'user_achievements' de la base de datos.
 * Cada fila indica que un usuario concreto desbloqueo un logro concreto.
 */
public class UserAchievement {

    private int id;
    private int userId;
    private int achievementId;
    private Timestamp unlockedAt;

    public UserAchievement() {}

    /**
     * Crea un nuevo registro de logro desbloqueado.
     *
     * @param id            Identificador unico del registro
     * @param userId        ID del usuario que desbloqueo el logro
     * @param achievementId ID del logro desbloqueado
     * @param unlockedAt    Fecha y hora en que se desbloqueo el logro
     */
    public UserAchievement(int id, int userId, int achievementId, Timestamp unlockedAt) {
        this.id = id;
        this.userId = userId;
        this.achievementId = achievementId;
        this.unlockedAt = unlockedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAchievementId() {
        return achievementId;
    }

    public void setAchievementId(int achievementId) {
        this.achievementId = achievementId;
    }

    public Timestamp getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(Timestamp unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
