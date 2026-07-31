import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reminder, ReminderCreate, ReminderUpdate } from '../models/reminder.model';

/**
 * Servicio que centraliza las llamadas al backend para los recordatorios.
 * Proporciona metodos para listar, crear y actualizar alarmas de habitos.
 */
@Injectable({
  providedIn: 'root'
})
export class ReminderService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/habit-tracker-backend/api/reminders';

  /**
   * Obtiene la lista de recordatorios de un habito.
   * @param habitId - El id del habito
   * @returns Observable con un array de Reminder
   */
  getRemindersByHabit(habitId: number): Observable<Reminder[]> {
    return this.http.get<Reminder[]>(`${this.apiUrl}?habitId=${habitId}`);
  }

  /**
   * Crea un nuevo recordatorio.
   * @param reminder - Los datos del recordatorio a crear
   * @returns Observable con el recordatorio creado
   */
  createReminder(reminder: ReminderCreate): Observable<Reminder> {
    return this.http.post<Reminder>(this.apiUrl, reminder);
  }

  /**
   * Actualiza un recordatorio existente.
   * @param id - El id del recordatorio a actualizar
   * @param reminder - Los datos actualizados del recordatorio
   * @returns Observable con el recordatorio actualizado
   */
  updateReminder(id: number, reminder: ReminderUpdate): Observable<Reminder> {
    return this.http.put<Reminder>(`${this.apiUrl}/${id}`, reminder);
  }
}
