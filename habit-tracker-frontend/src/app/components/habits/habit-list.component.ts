import { Component, Input, Output, EventEmitter, ViewChild, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Habit } from '../../models/habit.model';
import { HabitService } from '../../services/habit.service';
import { AchievementService } from '../../services/achievement.service';
import { CelebrationComponent } from '../shared/celebration/celebration.component';

/**
 * Componente que muestra una lista de tarjetas de habitos.
 * Cada tarjeta muestra el emoji, nombre y color del habito,
 * con un boton para marcar/desmarcar como completado,
 * y botones para editar y eliminar.
 * Si no hay habitos, muestra un mensaje motivador.
 * Al completar un habito consulta los logros recien desbloqueados
 * y muestra el modal de celebracion (logros o meta del dia cumplida).
 */
@Component({
  selector: 'app-habit-list',
  standalone: true,
  imports: [NgFor, NgIf, CelebrationComponent],
  templateUrl: './habit-list.component.html',
  styleUrl: './habit-list.component.scss'
})
export class HabitListComponent {
  private habitService = inject(HabitService);
  private achievementService = inject(AchievementService);

  /** Lista de habitos a mostrar */
  @Input() habits: Habit[] = [];

  /** Indica si los datos estan cargando */
  @Input() cargando: boolean = false;

  /** ID del usuario actual */
  @Input() userId: number = 0;

  /** Fecha en curso en formato YYYY-MM-DD */
  @Input() fecha: string = new Date().toISOString().split('T')[0];

  /** Conjunto de IDs de habitos completados en la fecha actual */
  @Input() completados: Set<number> = new Set();

  /** Emite el habito cuando se pulsa el boton editar */
  @Output() editar = new EventEmitter<Habit>();

  /** Emite el id del habito cuando se pulsa el boton eliminar */
  @Output() eliminar = new EventEmitter<number>();

  /** Emite cuando se marca o desmarca un habito */
  @Output() marcar = new EventEmitter<void>();

  /** IDs de habitos que estan siendo procesados (para evitar doble click) */
  marcando: Set<number> = new Set();

  /** ID del habito que se acaba de marcar (para animacion) */
  habitoCelebradoId: number | null = null;

  /** Mensaje de celebracion visible */
  mensajeCelebracion: string | null = null;

  /** Referencia al modal de celebracion para mostrar los logros nuevos */
  @ViewChild('celebracion') celebracionRef!: CelebrationComponent;

  /** Cola de celebraciones pendientes de mostrar */
  private colaCelebraciones: { mensaje: string; emoji: string }[] = [];

  /**
   * Marca o desmarca un habito como completado.
   * Llama al endpoint y actualiza el estado visual.
   * Al completar, consulta los logros recien desbloqueados y muestra
   * la celebracion correspondiente.
   * @param habit - El habito a marcar/desmarcar
   */
  toggleHabit(habit: Habit): void {
    if (this.marcando.has(habit.id)) {
      return;
    }

    this.marcando.add(habit.id);
    const nuevoEstado = !this.completados.has(habit.id);

    this.habitService.marcarHabit(habit.id, this.userId, this.fecha, nuevoEstado)
      .subscribe({
        next: () => {
          this.marcando.delete(habit.id);

          if (nuevoEstado) {
            this.completados.add(habit.id);
            this.mostrarCelebracion(habit);
            this.consultarLogrosNuevos();
          } else {
            this.completados.delete(habit.id);
          }

          this.marcar.emit();
        },
        error: () => {
          this.marcando.delete(habit.id);
        }
      });
  }

  /**
   * Muestra el mensaje de celebracion y la animacion durante 2 segundos.
   * @param habit - El habito que se acaba de completar
   */
  private mostrarCelebracion(habit: Habit): void {
    this.habitoCelebradoId = habit.id;
    this.mensajeCelebracion = '¡Genial! Un paso más 🌟';

    setTimeout(() => {
      this.habitoCelebradoId = null;
      this.mensajeCelebracion = null;
    }, 2000);
  }

  /**
   * Consulta los logros recien desbloqueados en esta sesion y los encola
   * para mostrarlos en el modal de celebracion. Si ademas este era el
   * ultimo habito del dia, encola tambien el mensaje de meta cumplida.
   */
  private consultarLogrosNuevos(): void {
    this.achievementService.getNewlyUnlocked().subscribe({
      next: (logros) => {
        for (const logro of logros) {
          this.encolarCelebracion(logro.name, logro.emoji);
        }

        if (this.esUltimoHabitoDelDia()) {
          this.encolarCelebracion('¡Meta del día cumplida!', '🏆');
        }

        this.mostrarSiguienteCelebracion();
      }
    });
  }

  /**
   * Comprueba si con el habito recien marcado ya estan completados todos
   * los habitos del dia.
   * @returns true si todos los habitos estan completados
   */
  private esUltimoHabitoDelDia(): boolean {
    return this.completados.size === this.habits.length;
  }

  /**
   * Añade una celebracion a la cola de pendientes.
   * @param mensaje - Mensaje que se mostrara en el modal
   * @param emoji - Emoji grande del modal
   */
  private encolarCelebracion(mensaje: string, emoji: string): void {
    this.colaCelebraciones.push({ mensaje, emoji });
  }

  /**
   * Muestra la siguiente celebracion de la cola, si queda alguna.
   * Lo invoca el template cuando se cierra el modal de celebracion.
   */
  mostrarSiguienteCelebracion(): void {
    const siguiente = this.colaCelebraciones.shift();
    if (!siguiente) {
      return;
    }

    this.celebracionRef.mensaje = siguiente.mensaje;
    this.celebracionRef.emoji = siguiente.emoji;
    this.celebracionRef.abrir();
  }

  /**
   * Verifica si un habito esta completado.
   * @param habitId - El id del habito
   * @returns true si el habito esta en el conjunto de completados
   */
  estaCompletado(habitId: number): boolean {
    return this.completados.has(habitId);
  }

  /**
   * Emite el evento para editar un habito.
   * @param habit - El habito a editar
   */
  onEditar(habit: Habit): void {
    this.editar.emit(habit);
  }

  /**
   * Emite el evento para eliminar un habito.
   * @param habitId - El id del habito a eliminar
   */
  onEliminar(habitId: number): void {
    this.eliminar.emit(habitId);
  }
}
