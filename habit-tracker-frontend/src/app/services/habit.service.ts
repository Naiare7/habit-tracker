import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Habit, HabitCreate, HabitUpdate } from '../models/habit.model';
import { HabitLog } from '../models/habit-log.model';

/**
 * Servicio que centraliza las llamadas al backend para la gestion de habitos.
 * Proporciona metodos CRUD para crear, listar, actualizar y eliminar habitos.
 */
@Injectable({
  providedIn: 'root'
})
export class HabitService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/habit-tracker-backend/api/habits';

  /**
   * Obtiene la lista de habitos activos de un usuario.
   * @param userId - El id del usuario
   * @returns Observable con un array de Habit
   */
  getHabitsByUser(userId: number): Observable<Habit[]> {
    return this.http.get<Habit[]>(`${this.apiUrl}?userId=${userId}`);
  }

  /**
   * Crea un nuevo habito.
   * @param habit - Los datos del habito a crear
   * @returns Observable con el habito creado
   */
  createHabit(habit: HabitCreate): Observable<Habit> {
    return this.http.post<Habit>(this.apiUrl, habit);
  }

  /**
   * Actualiza un habito existente.
   * @param id - El id del habito a actualizar
   * @param habit - Los datos actualizados del habito
   * @returns Observable con el habito actualizado
   */
  updateHabit(id: number, habit: HabitUpdate): Observable<Habit> {
    return this.http.put<Habit>(`${this.apiUrl}/${id}`, habit);
  }

  private apiUrlLogs = 'http://localhost:8080/habit-tracker-backend/api/habit-logs';

  /**
   * Obtiene los registros de habitos de un usuario en una fecha concreta.
   * @param userId - El id del usuario
   * @param date - La fecha en formato YYYY-MM-DD
   * @returns Observable con un array de HabitLog
   */
  getLogsForDay(userId: number, date: string): Observable<HabitLog[]> {
    return this.http.get<HabitLog[]>(`${this.apiUrlLogs}?userId=${userId}&date=${date}`);
  }

  /**
   * Elimina (borrado logico) un habito.
   * @param id - El id del habito a eliminar
   * @returns Observable con un mensaje de confirmacion
   */
  deleteHabit(id: number): Observable<{ mensaje: string }> {
    return this.http.delete<{ mensaje: string }>(`${this.apiUrl}/${id}`);
  }

  /**
   * Marca o desmarca un habito en una fecha concreta.
   * @param habitId - El id del habito
   * @param userId - El id del usuario
   * @param date - La fecha en formato YYYY-MM-DD
   * @param completed - true si esta completado, false si no
   * @returns Observable con el registro guardado
   */
  marcarHabit(habitId: number, userId: number, date: string, completed: boolean): Observable<HabitLog> {
    return this.http.post<HabitLog>(this.apiUrlLogs, {
      habitId,
      userId,
      date,
      completed
    });
  }
}
