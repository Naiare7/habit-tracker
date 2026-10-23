package com.habittracker.model;

import java.sql.Timestamp;

/**
 * Representa un logro disponible en la aplicacion.
 * Se corresponde con la tabla 'achievements' de la base de datos.
 */
public class Achievement {

    private int id;
    private String code;
    private String name;
    private String description;
    private String emoji;
    private String conditionType;
    private int conditionValue;
    private Timestamp createdAt;

    public Achievement() {}

    /**
     * Crea un nuevo logro con todos los campos.
     *
     * @param id             Identificador unico del logro
     * @param code           Codigo unico del logro (ej: "streak_7")
     * @param name           Nombre del logro
     * @param description    Descripcion de como obtenerlo
     * @param emoji          Emoji representativo
     * @param conditionType  Tipo de condicion para desbloquearlo
     * @param conditionValue Valor objetivo de la condicion
     * @param createdAt      Fecha de creacion
     */
    public Achievement(int id, String code, String name, String description,
                       String emoji, String conditionType, int conditionValue,
                       Timestamp createdAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.emoji = emoji;
        this.conditionType = conditionType;
        this.conditionValue = conditionValue;
        this.createdAt = createdAt;
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

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
