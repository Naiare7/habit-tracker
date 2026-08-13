package com.habittracker.model;

import java.sql.Timestamp;

/**
 * Representa un logro que la app puede otorgar al usuario.
 * Se corresponde con la tabla 'achievements' de la base de datos.
 * Los campos 'unlocked' y 'unlockedAt' son auxiliares y se rellenan
 * cuando el logro viene de la consulta de logros desbloqueados.
 */
public class Achievement {

    private int id;
    private String code;
    private String name;
    private String description;
    private String emoji;
    private String conditionType;
    private int conditionValue;
    private boolean unlocked;
    private Timestamp unlockedAt;

    public Achievement() {}

    /**
     * Crea un nuevo logro con todos los campos de la tabla.
     *
     * @param id             Identificador unico del logro
     * @param code           Codigo interno (ej: 'streak_7')
     * @param name           Nombre visible del logro
     * @param description    Descripcion del logro
     * @param emoji          Emoji que representa el logro
     * @param conditionType  Tipo de condicion a evaluar
     * @param conditionValue Valor que debe alcanzar la condicion
     */
    public Achievement(int id, String code, String name, String description,
                       String emoji, String conditionType, int conditionValue) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
        this.conditionType = conditionType;
        this.conditionValue = conditionValue;
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

    public boolean getUnlocked() {
        return unlocked;
    }

    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public Timestamp getUnlockedAt() {
        return unlockedAt;
    }

    public void setUnlockedAt(Timestamp unlockedAt) {
        this.unlockedAt = unlockedAt;
    }
}
