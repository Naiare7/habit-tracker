import { Component, OnInit, inject } from '@angular/core';
import { NgClass, NgFor, NgIf } from '@angular/common';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { StatsService } from '../../services/stats.service';
import { HabitService } from '../../services/habit.service';
import { AchievementService } from '../../services/achievement.service';
import { WeeklyChartComponent } from '../../components/stats/weekly-chart.component';
import { MonthlyChartComponent } from '../../components/stats/monthly-chart.component';
import { HabitCalendarComponent } from '../../components/stats/habit-calendar.component';
import { WeeklyStatsEntry, MonthlyStats, DailyStats } from '../../models/stats.model';
import { Habit } from '../../models/habit.model';
import { Achievement } from '../../models/achievement.model';

/**
 * Pantalla de estadisticas del usuario.
 * Compone el grafico semanal, el grafico mensual y el calendario de habitos,
 * con un selector para alternar entre la vista de semana y la de mes.
 * Tambien muestra la racha actual del habito mas largo y todos los logros
 * con su estado de desbloqueo.
 */
@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [NgClass, NgFor, NgIf, WeeklyChartComponent, MonthlyChartComponent, HabitCalendarComponent],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.scss'
})
export class StatsComponent implements OnInit {
  private authService = inject(AuthService);
  private statsService = inject(StatsService);
  private habitService = inject(HabitService);
  private achievementService = inject(AchievementService);

  /** Vista seleccionada en el selector (semana o mes) */
  vista: 'semana' | 'mes' = 'semana';

  /** Datos de la semana actual para el grafico de barras */
  datosSemanales: WeeklyStatsEntry[] = [];

  /** Datos de cumplimiento del mes actual para el grafico circular */
  datosMensuales: MonthlyStats | null = null;

  /** Datos diarios del mes actual para el calendario */
  datosCalendario: DailyStats[] = [];

  /** Logros del usuario con su estado de desbloqueo */
  logros: Achievement[] = [];

  /** Anho del mes actual */
  anho: number = new Date().getFullYear();

  /** Mes actual (1 = enero, 12 = diciembre) */
  mes: number = new Date().getMonth() + 1;

  /** Habito con la racha mas larga y el numero de dias consecutivos */
  habitoMasLargo: { habit: Habit; racha: number } | null = null;

  /** Indica si los datos siguen cargandose */
  cargando: boolean = true;

  userId: number = 0;

  /**
   * Inicializa el componente: obtiene el id del usuario y carga los datos.
   */
  ngOnInit(): void {
    const userId = this.authService.getCurrentUserId();
    this.userId = userId || 0;

    if (userId) {
      this.cargarDatos(userId);
    } else {
      this.cargando = false;
    }
  }

  /**
   * Carga todos los datos de las estadisticas del usuario: la semana,
   * el mes actual y la racha del habito mas largo.
   * @param userId - ID del usuario logueado
   */
  private cargarDatos(userId: number): void {
    this.cargando = true;

    this.statsService.getWeeklyStats(userId).subscribe({
      next: (semanales) => {
        this.datosSemanales = semanales;
      },
      error: () => {
        this.datosSemanales = [];
      }
    });

    this.statsService.getMonthlyStats(userId, this.anho, this.mes).subscribe({
      next: (mensuales) => {
        this.datosMensuales = mensuales;
      },
      error: () => {
        this.datosMensuales = null;
      }
    });

    this.statsService.getDailyCalendarStats(userId, this.anho, this.mes).subscribe({
      next: (calendario) => {
        this.datosCalendario = calendario;
      },
      error: () => {
        this.datosCalendario = [];
      }
    });

    this.cargarLogros(userId);
    this.cargarRachaMasLarga(userId);
  }

  /**
   * Carga todos los logros del usuario con su estado de desbloqueo.
   * @param userId - ID del usuario logueado
   */
  private cargarLogros(userId: number): void {
    this.achievementService.getAll(userId).subscribe({
      next: (logros) => {
        this.logros = logros;
      },
      error: () => {
        this.logros = [];
      }
    });
  }

  /**
   * Convierte la fecha de desbloqueo a un formato legible en espanol.
   * @param unlockedAt - Fecha de desbloqueo en formato del backend (puede ser null)
   * @returns La fecha formateada o cadena vacia si no hay fecha
   */
  formatearFecha(unlockedAt: string | null): string {
    if (!unlockedAt) {
      return '';
    }
    return new Date(unlockedAt).toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'long',
      year: 'numeric'
    });
  }

  /**
   * Busca el habito con la racha mas larga del usuario.
   * Obtiene todos los habitos, consulta la racha de cada uno
   * y se queda con el de mayor numero de dias consecutivos.
   * @param userId - ID del usuario logueado
   */
  private cargarRachaMasLarga(userId: number): void {
    this.habitService.getHabitsByUser(userId).subscribe({
      next: (habits) => {
        if (habits.length === 0) {
          this.habitoMasLargo = null;
          this.cargando = false;
          return;
        }

        // Consulta la racha de cada habito en paralelo
        const peticiones = habits.map(h => this.statsService.getStreak(h.id));
        forkJoin(peticiones).subscribe({
          next: (rachas) => {
            this.habitoMasLargo = this.encontrarHabitoMasLargo(
              habits, rachas.map(r => r.streak));
            this.cargando = false;
          },
          error: () => {
            this.habitoMasLargo = null;
            this.cargando = false;
          }
        });
      },
      error: () => {
        this.habitoMasLargo = null;
        this.cargando = false;
      }
    });
  }

  /**
   * Empareja cada habito con su racha y devuelve el de mayor valor.
   * @param habits - La lista de habitos del usuario
   * @param rachas - Las rachas en el mismo orden que los habitos
   * @returns El habito con la racha mas larga (null si no hay ninguno)
   */
  private encontrarHabitoMasLargo(habits: Habit[], rachas: number[]): { habit: Habit; racha: number } | null {
    let mejorHabit: Habit | null = null;
    let mejorRacha = 0;

    for (let i = 0; i < habits.length; i++) {
      if (rachas[i] > mejorRacha) {
        mejorRacha = rachas[i];
        mejorHabit = habits[i];
      }
    }

    return mejorHabit ? { habit: mejorHabit, racha: mejorRacha } : null;
  }

  /**
   * Cambia la vista actual entre semana y mes.
   * @param nuevaVista - La vista a la que se quiere cambiar
   */
  cambiarVista(nuevaVista: 'semana' | 'mes'): void {
    this.vista = nuevaVista;
  }
}
