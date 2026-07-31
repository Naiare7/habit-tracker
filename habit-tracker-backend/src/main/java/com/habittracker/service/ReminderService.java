package com.habittracker.service;

import com.habittracker.dao.ReminderDAO;
import com.habittracker.dto.ReminderDTO;
import com.habittracker.model.Reminder;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio con la logica de negocio para la gestion de recordatorios.
 * Se encarga de crear, listar, actualizar y eliminar recordatorios de habitos.
 */
public class ReminderService {

    private final ReminderDAO reminderDAO;

    public ReminderService() {
        this.reminderDAO = new ReminderDAO();
    }

    /**
     * Crea un nuevo recordatorio a partir de los datos del DTO.
     *
     * @param dto Datos del recordatorio a crear
     * @return ReminderDTO con los datos del recordatorio creado
     */
    public ReminderDTO createReminder(ReminderDTO dto) {
        Reminder recordatorio = new Reminder();
        recordatorio.setHabitId(dto.getHabitId());
        recordatorio.setUserId(dto.getUserId());
        recordatorio.setReminderTime(parsearHora(dto.getReminderTime()));
        recordatorio.setDaysOfWeek(dto.getDaysOfWeek());

        Reminder recordatorioCreado = reminderDAO.save(recordatorio);

        if (recordatorioCreado == null) {
            throw new RuntimeException("Error al crear el recordatorio");
        }

        return new ReminderDTO(recordatorioCreado);
    }

    /**
     * Obtiene todos los recordatorios de un habito.
     *
     * @param habitId ID del habito
     * @return Lista de ReminderDTO
     */
    public List<ReminderDTO> getRemindersByHabit(int habitId) {
        List<Reminder> recordatorios = reminderDAO.findByHabitId(habitId);
        return recordatorios.stream()
                .map(ReminderDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un recordatorio existente.
     *
     * @param dto Datos actualizados del recordatorio
     * @return ReminderDTO con los datos actualizados, o null si no existe
     */
    public ReminderDTO updateReminder(ReminderDTO dto) {
        Reminder recordatorio = new Reminder();
        recordatorio.setId(dto.getId());
        recordatorio.setUserId(dto.getUserId());
        recordatorio.setReminderTime(parsearHora(dto.getReminderTime()));
        recordatorio.setDaysOfWeek(dto.getDaysOfWeek());
        recordatorio.setIsActive(dto.getIsActive());

        boolean actualizado = reminderDAO.update(recordatorio);

        if (!actualizado) {
            return null;
        }

        return new ReminderDTO(recordatorio);
    }

    /**
     * Elimina un recordatorio de la base de datos.
     *
     * @param reminderId ID del recordatorio a eliminar
     * @return true si se elimino correctamente
     */
    public boolean deleteReminder(int reminderId) {
        return reminderDAO.delete(reminderId);
    }

    /**
     * Convierte un String con formato HH:mm a LocalTime.
     *
     * @param hora Hora en formato HH:mm
     * @return LocalTime correspondiente
     */
    private LocalTime parsearHora(String hora) {
        try {
            return LocalTime.parse(hora);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de hora invalido. Use HH:mm");
        }
    }
}
