import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * DTO de respuesta del backend tras registro o login.
 */
export interface AuthResponse {
  id: number;
  name: string;
  email: string;
  avatarEmoji: string;
  token?: string;
}

/**
 * Servicio de autenticacion que se comunica con el backend.
 * Proporciona metodos para registrarse, iniciar sesion y gestionar el token.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/habit-tracker-backend/api/auth';
  private tokenKey = 'habit-tracker-token';
  private userIdKey = 'habit-tracker-user-id';
  private nameKey = 'habit-tracker-name';

  /**
   * Registra un nuevo usuario.
   * @param name Nombre completo
   * @param email Email del usuario
   * @param password Contrasena en texto plano
   * @returns Observable con la respuesta del servidor
   */
  register(name: string, email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/register`, {
      name,
      email,
      password
    });
  }

  /**
   * Inicia sesion con email y contrasena.
   * Guarda el token en localStorage si el inicio es exitoso.
   * @param email Email del usuario
   * @param password Contrasena en texto plano
   * @returns Observable con la respuesta del servidor (incluye token)
   */
  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, {
      email,
      password
    });
  }

  /**
   * Guarda el token de sesion en localStorage.
   */
  guardarToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
  }

  /**
   * Obtiene el token de sesion desde localStorage.
   * @returns El token o null si no existe
   */
  obtenerToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  /**
   * Guarda el id del usuario en localStorage.
   */
  guardarUserId(id: number): void {
    localStorage.setItem(this.userIdKey, id.toString());
  }

  /**
   * Obtiene el id del usuario desde localStorage.
   * @returns El id del usuario o null si no existe
   */
  getCurrentUserId(): number | null {
    const id = localStorage.getItem(this.userIdKey);
    return id ? parseInt(id, 10) : null;
  }

  /**
   * Guarda el nombre del usuario en localStorage.
   * @param name Nombre del usuario
   */
  guardarNombre(name: string): void {
    localStorage.setItem(this.nameKey, name);
  }

  /**
   * Obtiene el nombre del usuario desde localStorage.
   * @returns El nombre del usuario o null si no existe
   */
  obtenerNombre(): string | null {
    return localStorage.getItem(this.nameKey);
  }

  /**
   * Elimina el token, el id y el nombre de sesion (cierra sesion).
   */
  cerrarSesion(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userIdKey);
    localStorage.removeItem(this.nameKey);
  }

  /**
   * Verifica si el usuario tiene una sesion activa.
   * @returns true si existe un token guardado
   */
  estaLogueado(): boolean {
    return this.obtenerToken() !== null;
  }
}
