import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  WeeklyStatsEntry,
  MonthlyStats,
  Streak,
  DailyStats
} from '../models/stats.model';

/**
 * Servicio que centraliza las llamadas al backend para las estadisticas.
 * Proporciona metodos para obtener estadisticas semanales, mensuales,
 * rachas y el cumplimiento diario del calendario.
 */
@Injectable({
  providedIn: 'root'
})
export class StatsService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/habit-tracker-backend/api/stats';

  /**
   * Obtiene cuantos habitos se completaron cada dia de la semana actual.
   * @param userId - El id del usuario
   * @returns Observable con un array de WeeklyStatsEntry
   */
  getWeeklyStats(userId: number): Observable<WeeklyStatsEntry[]> {
    return this.http.get<WeeklyStatsEntry[]>(`${this.apiUrl}/weekly?userId=${userId}`);
  }

  /**
   * Obtiene el porcentaje de cumplimiento de un mes concreto.
   * @param userId - El id del usuario
   * @param year - El anho del mes a consultar
   * @param month - El mes a consultar (1 = enero, 12 = diciembre)
   * @returns Observable con MonthlyStats
   */
  getMonthlyStats(userId: number, year: number, month: number): Observable<MonthlyStats> {
    return this.http.get<MonthlyStats>(
      `${this.apiUrl}/monthly?userId=${userId}&year=${year}&month=${month}`);
  }

  /**
   * Obtiene la racha de dias consecutivos de un habito.
   * @param habitId - El id del habito
   * @returns Observable con la racha del habito
   */
  getStreak(habitId: number): Observable<Streak> {
    return this.http.get<Streak>(`${this.apiUrl}/streak?habitId=${habitId}`);
  }

  /**
   * Obtiene el nivel de cumplimiento de cada dia de un mes para el calendario.
   * @param userId - El id del usuario
   * @param year - El anho del mes a consultar
   * @param month - El mes a consultar (1 = enero, 12 = diciembre)
   * @returns Observable con un array de DailyStats
   */
  getDailyCalendarStats(userId: number, year: number, month: number): Observable<DailyStats[]> {
    return this.http.get<DailyStats[]>(
      `${this.apiUrl}/calendar?userId=${userId}&year=${year}&month=${month}`);
  }
}
