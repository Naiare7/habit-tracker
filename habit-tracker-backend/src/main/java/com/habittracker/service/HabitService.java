package com.habittracker.service;

import com.habittracker.dao.HabitDAO;
import com.habittracker.dto.HabitCreateDTO;
import com.habittracker.dto.HabitResponseDTO;
import com.habittracker.dto.HabitUpdateDTO;
import com.habittracker.model.Habit;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio con la logica de negocio para la gestion de habitos.
 * Se encarga de crear, listar, actualizar y eliminar habitos logicamente.
 */
public class HabitService {

    private final HabitDAO habitDAO;

    public HabitService() {
        this.habitDAO = new HabitDAO();
    }

    /**
     * Crea un nuevo habito a partir de los datos del DTO.
     *
     * @param dto Datos del formulario de creacion
     * @return HabitResponseDTO con los datos del habito creado
     */
    public HabitResponseDTO createHabit(HabitCreateDTO dto) {
        Habit habito = new Habit();
        habito.setUserId(dto.getUserId());
        habito.setName(dto.getName());
        habito.setDescription(dto.getDescription());
        habito.setEmoji(dto.getEmoji());
        habito.setFrequency(dto.getFrequency());
        habito.setTargetDays(dto.getTargetDays());
        habito.setColor(dto.getColor());

        Habit habitoCreado = habitDAO.save(habito);

        if (habitoCreado == null) {
            throw new RuntimeException("Error al crear el habito");
        }

        return new HabitResponseDTO(habitoCreado);
    }

    /**
     * Obtiene todos los habitos activos de un usuario.
     *
     * @param userId ID del usuario
     * @return Lista de HabitResponseDTO
     */
    public List<HabitResponseDTO> getHabitsByUser(int userId) {
        List<Habit> habitos = habitDAO.findByUserId(userId);
        return habitos.stream()
                .map(HabitResponseDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Actualiza un habito existente.
     *
     * @param dto Datos actualizados del habito
     * @return HabitResponseDTO con los datos actualizados, o null si no existe
     */
    public HabitResponseDTO updateHabit(HabitUpdateDTO dto) {
        Habit habito = new Habit();
        habito.setId(dto.getId());
        habito.setName(dto.getName());
        habito.setDescription(dto.getDescription());
        habito.setEmoji(dto.getEmoji());
        habito.setFrequency(dto.getFrequency());
        habito.setTargetDays(dto.getTargetDays());
        habito.setColor(dto.getColor());

        boolean actualizado = habitDAO.update(habito);

        if (!actualizado) {
            return null;
        }

        return new HabitResponseDTO(habito);
    }

    /**
     * Elimina un habito logicamente (is_active = false).
     *
     * @param habitId ID del habito a eliminar
     * @return true si se elimino correctamente
     */
    public boolean deleteHabit(int habitId) {
        return habitDAO.deactivate(habitId);
    }
}
