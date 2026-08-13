import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Achievement } from '../models/achievement.model';

/**
 * Servicio que centraliza las llamadas al backend para la gestion de logros.
 * Proporciona metodos para consultar todos los logros con su estado de
 * desbloqueo y los logros recien desbloqueados en la sesion actual.
 */
@Injectable({
  providedIn: 'root'
})
export class AchievementService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/habit-tracker-backend/api/achievements';

  /**
   * Obtiene todos los logros disponibles con su estado de desbloqueo.
   * @param userId - El id del usuario
   * @returns Observable con un array de Achievement
   */
  getAll(userId: number): Observable<Achievement[]> {
    return this.http.get<Achievement[]>(`${this.apiUrl}?userId=${userId}`);
  }

  /**
   * Obtiene los logros recien desbloqueados en esta sesion.
   * La lista se vacia en el backend despues de la consulta.
   * @returns Observable con un array de Achievement
   */
  getNewlyUnlocked(): Observable<Achievement[]> {
    return this.http.get<Achievement[]>(`${this.apiUrl}/new`);
  }
}
