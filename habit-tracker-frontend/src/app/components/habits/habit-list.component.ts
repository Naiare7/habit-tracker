import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgFor, NgIf } from '@angular/common';
import { Habit } from '../../models/habit.model';

/**
 * Componente que muestra una lista de tarjetas de habitos.
 * Cada tarjeta muestra el emoji, nombre y color del habito,
 * con botones para editar y eliminar.
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
  /** Lista de habitos a mostrar */
  @Input() habits: Habit[] = [];

  /** Indica si los datos estan cargando */
  @Input() cargando: boolean = false;

  /** Emite el habito cuando se pulsa el boton editar */
  @Output() editar = new EventEmitter<Habit>();

  /** Emite el id del habito cuando se pulsa el boton eliminar */
  @Output() eliminar = new EventEmitter<number>();

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
