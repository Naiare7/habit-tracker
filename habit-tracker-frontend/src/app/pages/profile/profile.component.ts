import { Component, OnInit, inject } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { forkJoin } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { HabitService } from '../../services/habit.service';
import { ReminderService } from '../../services/reminder.service';
import { NotificationService } from '../../services/notification.service';
import { HabitRemindersComponent } from '../../components/habits/habit-reminders.component';
import { Habit } from '../../models/habit.model';
import { Reminder, ReminderUpdate } from '../../models/reminder.model';

/**
 * Dias de la semana para mostrar los recordatorios.
 * 0 = Domingo, 1 = Lunes, ..., 6 = Sabado.
 */
interface DiaSemana {
  valor: number;
  abreviatura: string;
  nombre: string;
}

const DIAS_SEMANA: DiaSemana[] = [
  { valor: 0, abreviatura: 'D', nombre: 'Domingo' },
  { valor: 1, abreviatura: 'L', nombre: 'Lunes' },
  { valor: 2, abreviatura: 'M', nombre: 'Martes' },
  { valor: 3, abreviatura: 'X', nombre: 'Miércoles' },
  { valor: 4, abreviatura: 'J', nombre: 'Jueves' },
  { valor: 5, abreviatura: 'V', nombre: 'Viernes' },
  { valor: 6, abreviatura: 'S', nombre: 'Sábado' }
];

/**
 * Pantalla de perfil del usuario.
 * Muestra el nombre y el avatar del usuario, y lista todos sus habitos
 * con sus recordatorios configurados (toggle on/off).
 * Un boton permite editar los recordatorios de cada habito.
 */
@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NgIf, NgFor, HabitRemindersComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.scss'
})
export class ProfileComponent implements OnInit {
  private authService = inject(AuthService);
  private habitService = inject(HabitService);
  private reminderService = inject(ReminderService);
  private notificationService = inject(NotificationService);

  diasSemana = DIAS_SEMANA;

  /** Nombre del usuario logueado */
  nombre: string = '';

  /** Emoji del avatar del usuario */
  avatarEmoji: string = '🌸';

  /** Lista de habitos del usuario */
  habits: Habit[] = [];

  /** Recordatorios agrupados por id de habito */
  recordatoriosPorHabito: Map<number, Reminder[]> = new Map();

  /** Indica si los datos estan cargando */
  cargando: boolean = true;

  /** Id del habito cuyo editor de recordatorios esta abierto */
  editandoHabitoId: number | null = null;

  /** Ids de recordatorios que se estan actualizando (evita doble click) */
  actualizando: Set<number> = new Set();

  /** Timeouts programados por recordatorio para poder cancelarlos */
  tiemposProgramados: Map<number, number> = new Map();

  userId: number = 0;

  /**
   * Inicializa el componente: obtiene el nombre y el id del usuario
   * y carga sus habitos con sus recordatorios.
   */
  ngOnInit(): void {
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
   * Carga los habitos del usuario y, despues, los recordatorios de cada uno.
   * @param userId - ID del usuario logueado
   */
  private cargarDatos(userId: number): void {
    this.cargando = true;

    this.habitService.getHabitsByUser(userId).subscribe({
      next: (habits) => {
        this.habits = habits;

        if (habits.length === 0) {
          this.cargando = false;
          return;
        }

        this.cargarRecordatorios(habits);
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  /**
   * Consulta los recordatorios de todos los habitos en paralelo
   * y los agrupa en el mapa recordatoriosPorHabito.
   * @param habits - La lista de habitos del usuario
   */
  private cargarRecordatorios(habits: Habit[]): void {
    const peticiones = habits.map(h => this.reminderService.getRemindersByHabit(h.id));
    const idsHabitos = habits.map(h => h.id);

    forkJoin(peticiones).subscribe({
      next: (resultados) => {
        this.recordatoriosPorHabito = new Map();
        for (let i = 0; i < idsHabitos.length; i++) {
          this.recordatoriosPorHabito.set(idsHabitos[i], resultados[i]);
        }
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  /**
   * Devuelve los recordatorios de un habito concreto.
   * @param habitId - El id del habito
   * @returns La lista de recordatorios del habito (vacia si no hay)
   */
  recordatoriosDe(habitId: number): Reminder[] {
    return this.recordatoriosPorHabito.get(habitId) || [];
  }

  /**
   * Convierte los dias de un recordatorio en sus abreviaturas.
   * @param daysOfWeek - Array de dias (0-6)
   * @returns Cadena con las abreviaturas separadas por espacio
   */
  diasDelRecordatorio(daysOfWeek: number[]): string {
    if (!daysOfWeek || daysOfWeek.length === 0) {
      return 'Todos los días';
    }

    return daysOfWeek.map(dia => {
      const encontrado = DIAS_SEMANA.find(d => d.valor === dia);
      return encontrado ? encontrado.abreviatura : '';
    }).join(' ');
  }

  /**
   * Activa o desactiva una alarma desde la lista del perfil.
   * Al activarla, solicita el permiso y programa la notificacion.
   * Al desactivarla, cancela la notificacion programada.
   * @param recordatorio - El recordatorio a activar/desactivar
   * @param habit - El habito al que pertenece el recordatorio
   */
  toggleAlarma(recordatorio: Reminder, habit: Habit): void {
    if (this.actualizando.has(recordatorio.id)) {
      return;
    }

    this.actualizando.add(recordatorio.id);
    const nuevoEstado = !recordatorio.isActive;

    if (nuevoEstado) {
      this.activarAlarma(recordatorio, habit);
    } else {
      this.desactivarAlarma(recordatorio);
    }
  }

  /**
   * Programa la notificacion de una alarma y la activa en el backend.
   * @param recordatorio - El recordatorio a activar
   * @param habit - El habito al que pertenece el recordatorio
   */
  private activarAlarma(recordatorio: Reminder, habit: Habit): void {
    this.notificationService.solicitarPermiso().then(permiso => {
      if (permiso !== 'granted') {
        this.actualizando.delete(recordatorio.id);
        return;
      }

      const timeoutId = this.notificationService.scheduleReminder(
        habit.name,
        recordatorio.reminderTime
      );
      this.tiemposProgramados.set(recordatorio.id, timeoutId);
      this.guardarEstado(recordatorio, true);
    });
  }

  /**
   * Cancela la notificacion de una alarma y la desactiva en el backend.
   * @param recordatorio - El recordatorio a desactivar
   */
  private desactivarAlarma(recordatorio: Reminder): void {
    const timeoutId = this.tiemposProgramados.get(recordatorio.id);
    if (timeoutId !== undefined) {
      this.notificationService.cancelarRecordatorio(timeoutId);
      this.tiemposProgramados.delete(recordatorio.id);
    }

    this.guardarEstado(recordatorio, false);
  }

  /**
   * Guarda el nuevo estado de la alarma en el backend y actualiza la lista.
   * @param recordatorio - El recordatorio a actualizar
   * @param isActive - El nuevo estado de la alarma
   */
  private guardarEstado(recordatorio: Reminder, isActive: boolean): void {
    const datos: ReminderUpdate = {
      id: recordatorio.id,
      userId: recordatorio.userId,
      reminderTime: recordatorio.reminderTime,
      daysOfWeek: recordatorio.daysOfWeek || [],
      isActive
    };

    this.reminderService.updateReminder(recordatorio.id, datos).subscribe({
      next: () => {
        recordatorio.isActive = isActive;
        this.actualizando.delete(recordatorio.id);
      },
      error: () => {
        this.actualizando.delete(recordatorio.id);
      }
    });
  }

  /**
   * Abre o cierra el editor de recordatorios de un habito.
   * Al abrirlo, recarga los recordatorios del habito para mantenerlos al dia.
   * @param habitId - El id del habito
   */
  editarRecordatorio(habitId: number): void {
    if (this.editandoHabitoId === habitId) {
      this.editandoHabitoId = null;
      return;
    }

    this.editandoHabitoId = habitId;
    const habit = this.habits.find(h => h.id === habitId);
    if (habit) {
      this.cargarRecordatorios([habit]);
    }
  }
}
