package com.habittracker.dto;

import com.habittracker.model.Achievement;

/**
 * DTO que representa un logro con su estado de desbloqueo.
 * Se utiliza para mostrar todos los logros al usuario indicando
 * cuales ya ha desbloqueado y cuales le faltan.
 */
public class AchievementWithStatusDTO {

    private int id;
    private String code;
    private String name;
    private String description;
    private String emoji;
    private String conditionType;
    private int conditionValue;
    private boolean unlocked;
    private String unlockedAt;

    public AchievementWithStatusDTO() {}

    /**
     * Crea un DTO a partir de un logro y su estado de desbloqueo.
     *
     * @param logro       El logro a representar
     * @param unlocked    true si el usuario ya lo ha desbloqueado
     * @param unlockedAt  Fecha de desbloqueo en formato texto, o null si no esta desbloqueado
     */
    public AchievementWithStatusDTO(Achievement logro, boolean unlocked, String unlockedAt) {
        this.id = logro.getId();
        this.code = logro.getCode();
        this.name = logro.getName();
        this.description = logro.getDescription();
        this.emoji = logro.getEmoji();
        this.conditionType = logro.getConditionType();
        this.conditionValue = logro.getConditionValue();
        this.unlocked = unlocked;
        this.unlockedAt = unlockedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public int getConditionValue() {
        return conditionValue;
    }

    public void setConditionValue(int conditionValue) {
        this.conditionValue = conditionValue;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public String getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(String unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
