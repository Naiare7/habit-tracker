import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { NgIf, NgClass } from '@angular/common';
import { AuthService } from '../../services/auth.service';
import { HabitService } from '../../services/habit.service';
import { HabitListComponent } from '../../components/habits/habit-list.component';
import { Habit } from '../../models/habit.model';

/**
 * Pantalla principal del dashboard.
 * Muestra el saludo personalizado segun la hora del dia,
 * la barra de progreso circular y la lista de habitos del dia.
 */
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgIf, NgClass, HabitListComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  private authService = inject(AuthService);
  private habitService = inject(HabitService);
  private router = inject(Router);

  nombre: string = '';
  saludo: string = '';
  emojiSaludo: string = '';
  fraseSaludo: string = '';

  habits: Habit[] = [];
  completados: Set<number> = new Set();
  cargando: boolean = true;

  /** Total de habitos activos */
  totalHabitos: number = 0;

  /** Cuantos habitos estan completados hoy */
  completadosCount: number = 0;

  /** Porcentaje de progreso (0-100) */
  progreso: number = 0;

  /** Longitud de la circunferencia del arco SVG de progreso (2 * PI * radio 54) */
  circunferencia: number = 2 * Math.PI * 54;

  /** Fecha de hoy en formato YYYY-MM-DD */
  fechaHoy: string = new Date().toISOString().split('T')[0];

  userId: number = 0;

  /**
   * Inicializa el componente: establece el saludo y carga los datos.
   */
  ngOnInit(): void {
    this.obtenerSaludo();
    this.nombre = this.authService.obtenerNombre() || 'amiga';
    const userId = this.authService.getCurrentUserId();
    this.userId = userId || 0;

    if (userId) {
      this.cargarDatos(userId);
    } else {
      this.cargando = false;
    }
  }

  /**
   * Establece el saludo y emoji segun la hora del dia.
   */
  private obtenerSaludo(): void {
    const hora = new Date().getHours();

    if (hora >= 6 && hora < 12) {
      this.saludo = '¡Buenos días';
      this.emojiSaludo = '☀️';
      this.fraseSaludo = '¿Lista para empezar?';
    } else if (hora >= 12 && hora < 18) {
      this.saludo = '¡Buenas tardes';
      this.emojiSaludo = '🌤️';
      this.fraseSaludo = '¿Cómo vas hoy?';
    } else if (hora >= 18 && hora < 21) {
      this.saludo = '¡Buenas tardes';
      this.emojiSaludo = '🌅';
      this.fraseSaludo = 'Repasa tu día';
    } else {
      this.saludo = '¡Buenas noches';
      this.emojiSaludo = '🌙';
      this.fraseSaludo = 'Repasa tu día';
    }
  }

  /**
   * Carga los habitos del usuario y los registros de hoy.
   * @param userId - ID del usuario logueado
   */
  private cargarDatos(userId: number): void {
    this.cargando = true;

    this.habitService.getHabitsByUser(userId).subscribe({
      next: (habits) => {
        this.habits = habits;
        this.totalHabitos = habits.length;
        this.cargarLogs(userId);
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  /**
   * Carga los registros de hoy para saber que habitos estan completados.
   * @param userId - ID del usuario logueado
   */
  private cargarLogs(userId: number): void {
    this.habitService.getLogsForDay(userId, this.fechaHoy).subscribe({
      next: (logs) => {
        this.completados = new Set(
          logs.filter(l => l.completed).map(l => l.habitId)
        );
        this.completadosCount = this.completados.size;
        this.actualizarProgreso();
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  /**
   * Actualiza el contador de progreso.
   */
  private actualizarProgreso(): void {
    this.completadosCount = this.completados.size;
    this.progreso = this.totalHabitos > 0
      ? Math.round((this.completadosCount / this.totalHabitos) * 100)
      : 0;
  }

  /**
   * Navega a la pagina de creacion de habito.
   */
  irACrearHabito(): void {
    this.router.navigate(['/dashboard']);
  }

  /**
   * Recarga los datos despues de marcar/desmarcar un habito.
   */
  recargarLogs(): void {
    this.cargarLogs(this.userId);
  }

  /**
   * Maneja la edicion de un habito.
   * @param habit - El habito a editar
   */
  onEditar(habit: Habit): void {
    console.log('Editar habito:', habit);
  }

  /**
   * Maneja la eliminacion de un habito.
   * @param habitId - El id del habito a eliminar
   */
  onEliminar(habitId: number): void {
    this.habitService.deleteHabit(habitId).subscribe({
      next: () => {
        this.cargarDatos(this.userId);
      }
    });
  }

  /**
   * Recarga los datos despues de marcar un habito.
   */
  onMarcar(): void {
    this.recargarLogs();
  }
}
