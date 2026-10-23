package com.habittracker.model;

import java.sql.Timestamp;

/**
 * Representa la relacion entre un usuario y un logro desbloqueado.
 * Se corresponde con la tabla 'user_achievements' de la base de datos.
 */
public class UserAchievement {

    private int id;
    private int userId;
    private int achievementId;
    private Timestamp unlockedAt;

    public UserAchievement() {}

    /**
     * Crea una nueva relacion usuario-logro con todos los campos.
     *
     * @param id            Identificador unico de la relacion
     * @param userId        Identificador del usuario
     * @param achievementId Identificador del logro
     * @param unlockedAt    Fecha en que se desbloqueo el logro
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
