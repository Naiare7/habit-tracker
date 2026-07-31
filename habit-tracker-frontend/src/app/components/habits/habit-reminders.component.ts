import { Component, Input, OnInit, inject } from '@angular/core';
import { NgIf, NgFor } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Habit } from '../../models/habit.model';
import { Reminder, ReminderCreate, ReminderUpdate } from '../../models/reminder.model';
import { ReminderService } from '../../services/reminder.service';
import { NotificationService } from '../../services/notification.service';

/**
 * Dias de la semana para el selector de alarmas.
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
 * Componente que gestiona las alarmas (recordatorios) de un habito.
 * Muestra la lista de recordatorios del habito seleccionado,
 * permite anadir una nueva alarma (hora + dias de la semana)
 * y activar/desactivar cada alarma con un toggle.
 * Al activar, programa la notificacion en el navegador.
 */
@Component({
  selector: 'app-habit-reminders',
  standalone: true,
  imports: [NgIf, NgFor, FormsModule],
  templateUrl: './habit-reminders.component.html',
  styleUrl: './habit-reminders.component.scss'
})
export class HabitRemindersComponent implements OnInit {
  private reminderService = inject(ReminderService);
  private notificationService = inject(NotificationService);

  /** Habito cuyas alarmas se gestionan */
  @Input() habit: Habit | null = null;

  /** ID del usuario propietario */
  @Input() userId: number = 0;

  diasSemana = DIAS_SEMANA;

  /** Lista de recordatorios del habito */
  recordatorios: Reminder[] = [];

  /** Indica si los datos estan cargando */
  cargando: boolean = true;

  /** Hora de la nueva alarma (formato HH:mm) */
  hora: string = '';

  /** Dias de la semana seleccionados para la nueva alarma */
  diasSeleccionados: Set<number> = new Set();

  /** Indica si se esta guardando una nueva alarma */
  guardando: boolean = false;

  /** Mensaje de error o aviso para el usuario */
  mensaje: string = '';

  /** Ids de recordatorios que se estan actualizando (evita doble click) */
  actualizando: Set<number> = new Set();

  /** Timeouts programados por recordatorio para poder cancelarlos */
  tiemposProgramados: Map<number, number> = new Map();

  /**
   * Inicializa el componente cargando los recordatorios del habito.
   */
  ngOnInit(): void {
    this.cargarRecordatorios();
  }

  /**
   * Carga los recordatorios del habito desde el backend.
   */
  private cargarRecordatorios(): void {
    if (!this.habit) {
      this.cargando = false;
      return;
    }

    this.cargando = true;

    this.reminderService.getRemindersByHabit(this.habit.id).subscribe({
      next: (recordatorios) => {
        this.recordatorios = recordatorios;
        this.cargando = false;
      },
      error: () => {
        this.cargando = false;
      }
    });
  }

  /**
   * Marca o desmarca un dia de la semana en el formulario.
   * @param dia - El valor del dia (0-6)
   */
  toggleDia(dia: number): void {
    if (this.diasSeleccionados.has(dia)) {
      this.diasSeleccionados.delete(dia);
    } else {
      this.diasSeleccionados.add(dia);
    }
  }

  /**
   * Verifica si un dia esta seleccionado en el formulario.
   * @param dia - El valor del dia (0-6)
   * @returns true si el dia esta seleccionado
   */
  diaSeleccionado(dia: number): boolean {
    return this.diasSeleccionados.has(dia);
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
   * Anade una nueva alarma con la hora y los dias seleccionados.
   * Valida que la hora sea obligatoria antes de guardar.
   */
  agregarAlarma(): void {
    this.mensaje = '';

    if (!this.habit) {
      return;
    }

    if (!this.hora) {
      this.mensaje = 'Elige una hora para la alarma 🕐';
      return;
    }

    this.guardando = true;

    const datos: ReminderCreate = {
      habitId: this.habit.id,
      userId: this.userId,
      reminderTime: this.hora,
      daysOfWeek: Array.from(this.diasSeleccionados).sort()
    };

    this.reminderService.createReminder(datos).subscribe({
      next: () => {
        this.guardando = false;
        this.hora = '';
        this.diasSeleccionados = new Set();
        this.cargarRecordatorios();
      },
      error: () => {
        this.guardando = false;
        this.mensaje = 'No se pudo guardar la alarma 😢';
      }
    });
  }

  /**
   * Activa o desactiva una alarma.
   * Al activarla, solicita el permiso de notificaciones y la programa.
   * Al desactivarla, cancela la notificacion programada.
   * @param recordatorio - El recordatorio a activar/desactivar
   */
  toggleAlarma(recordatorio: Reminder): void {
    if (this.actualizando.has(recordatorio.id)) {
      return;
    }

    this.actualizando.add(recordatorio.id);
    this.mensaje = '';

    const nuevoEstado = !recordatorio.isActive;

    if (nuevoEstado) {
      this.activarAlarma(recordatorio);
    } else {
      this.desactivarAlarma(recordatorio);
    }
  }

  /**
   * Programa la notificacion de una alarma y la activa en el backend.
   * @param recordatorio - El recordatorio a activar
   */
  private activarAlarma(recordatorio: Reminder): void {
    this.notificationService.solicitarPermiso().then(permiso => {
      if (permiso !== 'granted') {
        this.actualizando.delete(recordatorio.id);
        this.mensaje = 'Activa las notificaciones del navegador para recibir avisos 🔕';
        return;
      }

      const timeoutId = this.notificationService.scheduleReminder(
        this.habit?.name || 'tus hábitos',
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
        this.mensaje = 'No se pudo actualizar la alarma 😢';
      }
    });
  }
}
