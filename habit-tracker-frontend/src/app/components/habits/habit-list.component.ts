import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Habit } from '../../models/habit.model';
import { HabitService } from '../../services/habit.service';

/**
 * Componente que muestra una lista de tarjetas de habitos.
 * Cada tarjeta muestra el emoji, nombre y color del habito,
 * con un boton para marcar/desmarcar como completado,
 * y botones para editar y eliminar.
 * Si no hay habitos, muestra un mensaje motivador.
 */
@Component({
  selector: 'app-habit-list',
  standalone: true,
  imports: [NgFor, NgIf],
  templateUrl: './habit-list.component.html',
  styleUrl: './habit-list.component.scss'
})
export class HabitListComponent {
  private habitService = inject(HabitService);

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

  /**
   * Marca o desmarca un habito como completado.
   * Llama al endpoint y actualiza el estado visual.
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
