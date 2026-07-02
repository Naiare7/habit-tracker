package com.habittracker.service;

import com.habittracker.dao.HabitLogDAO;
import com.habittracker.dto.HabitLogDTO;
import com.habittracker.model.HabitLog;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio con la logica de negocio para los registros diarios de habitos.
 * Permite marcar un habito como completado y consultar los registros de un dia.
 */
public class HabitLogService {

    private final HabitLogDAO habitLogDAO;

    public HabitLogService() {
        this.habitLogDAO = new HabitLogDAO();
    }

    /**
     * Marca un habito como completado o no completado para una fecha concreta.
     * Si ya existe un registro para ese habito y fecha, lo actualiza.
     * Si no existe, lo crea.
     *
     * @param habitId   ID del habito
     * @param userId    ID del usuario propietario
     * @param date      Fecha del registro
     * @param completed true si esta completado, false si no
     * @return HabitLogDTO con los datos guardados
     */
    public HabitLogDTO markHabit(int habitId, int userId, LocalDate date, boolean completed) {
        HabitLog log = new HabitLog();
        log.setHabitId(habitId);
        log.setUserId(userId);
        log.setCompletedDate(date);
        log.setCompleted(completed);
        log.setNotes(null);

        HabitLog logGuardado = habitLogDAO.saveOrUpdate(log);

        if (logGuardado == null) {
            throw new RuntimeException("Error al guardar el registro del habito");
        }

        return convertirADTO(logGuardado);
    }

    /**
     * Obtiene todos los registros de un usuario para un dia concreto.
     *
     * @param userId ID del usuario
     * @param date   Fecha a consultar
     * @return Lista de HabitLogDTO con los registros del dia
     */
    public List<HabitLogDTO> getLogsForDay(int userId, LocalDate date) {
        List<HabitLog> registros = habitLogDAO.findByUserIdAndDateRange(userId, date, date);
        return registros.stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un HabitLog (modelo) a HabitLogDTO.
     *
     * @param log El objeto HabitLog del modelo
     * @return Un HabitLogDTO con los mismos datos
     */
    private HabitLogDTO convertirADTO(HabitLog log) {
        HabitLogDTO dto = new HabitLogDTO();
        dto.setHabitId(log.getHabitId());
        dto.setUserId(log.getUserId());
        dto.setDate(log.getCompletedDate().toString());
        dto.setCompleted(log.isCompleted());
        dto.setNotes(log.getNotes());
        return dto;
    }
}
