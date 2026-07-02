import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgIf, NgFor } from '@angular/common';
import { Habit, HabitCreate, HabitUpdate } from '../../models/habit.model';

/**
 * Lista de emojis predefinidos para el selector visual.
 */
const EMOJIS_PREDEFINIDOS: string[] = [
  '🌱', '💪', '📚', '🧘‍♀️', '🏃‍♂️', '🎨', '✍️', '🥗',
  '💧', '😴', '🎵', '🧠', '🌍', '💛', '⭐', '🌟',
  '🔥', '🎯', '🏆', '✨', '🌸', '🍎', '🎸', '🧹',
  '📝', '☀️', '🌙', '🎮', '🧁', '🍵'
];

/**
 * Colores predefinidos de la paleta cozy.
 */
const COLORES_PREDEFINIDOS: string[] = [
  '#FFB347', '#A8C5A0', '#C3B1E1', '#F2C4CE',
  '#B8D4E8', '#FF6B6B', '#FFF3B0', '#8B6F47'
];

/**
 * Dias de la semana para la frecuencia semanal.
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
 * Componente de formulario para crear o editar un habito.
 * Si recibe un habito como entrada, rellena los campos (modo edicion).
 * Si no, muestra el formulario vacio (modo creacion).
 */
@Component({
  selector: 'app-habit-form',
  standalone: true,
  imports: [FormsModule, NgIf, NgFor],
  templateUrl: './habit-form.component.html',
  styleUrl: './habit-form.component.scss'
})
export class HabitFormComponent implements OnInit {
  /** Habito a editar. Si es null, el formulario esta en modo creacion. */
  @Input() habit: Habit | null = null;

  /** Emite los datos del habito al guardar (crear o actualizar). */
  @Output() guardar = new EventEmitter<HabitCreate | HabitUpdate>();

  /** Emite cuando se cancela la operacion. */
  @Output() cancelar = new EventEmitter<void>();

  emojis = EMOJIS_PREDEFINIDOS;
  colores = COLORES_PREDEFINIDOS;
  diasSemana = DIAS_SEMANA;

  nombre: string = '';
  descripcion: string = '';
  emoji: string = '';
  frecuencia: string = 'daily';
  targetDays: Set<number> = new Set();
  color: string = COLORES_PREDEFINIDOS[0];
  cargando: boolean = false;
  mensajeError: string = '';

  /**
   * Indica si el formulario esta en modo edicion.
   */
  get modoEdicion(): boolean {
    return this.habit !== null;
  }

  /**
   * Indica si se deben mostrar los dias de la semana (frecuencia semanal).
   */
  get mostrarDias(): boolean {
    return this.frecuencia === 'weekly';
  }

  /**
   * Inicializa los campos del formulario.
   * Si hay un habito para editar, rellena los campos con sus valores.
   */
  ngOnInit(): void {
    if (this.habit) {
      this.nombre = this.habit.name;
      this.descripcion = this.habit.description || '';
      this.emoji = this.habit.emoji;
      this.frecuencia = this.habit.frequency;
      this.targetDays = new Set(this.habit.targetDays || []);
      this.color = this.habit.color || COLORES_PREDEFINIDOS[0];
    }
  }

  /**
   * Selecciona o cambia el emoji del habito.
   * @param emoji - El emoji seleccionado
   */
  seleccionarEmoji(emoji: string): void {
    this.emoji = emoji;
  }

  /**
   * Marca o desmarca un dia de la semana para la frecuencia semanal.
   * @param dia - El valor del dia (0-6)
   */
  toggleDia(dia: number): void {
    if (this.targetDays.has(dia)) {
      this.targetDays.delete(dia);
    } else {
      this.targetDays.add(dia);
    }
  }

  /**
   * Verifica si un dia esta seleccionado.
   * @param dia - El valor del dia (0-6)
   * @returns true si el dia esta en la seleccion
   */
  diaSeleccionado(dia: number): boolean {
    return this.targetDays.has(dia);
  }

  /**
   * Selecciona un color predefinido.
   * @param color - El codigo hexadecimal del color
   */
  seleccionarColor(color: string): void {
    this.color = color;
  }

  /**
   * Valida los campos y emite el evento guardar.
   * Si hay errores de validacion, muestra un mensaje y no emite.
   */
  onSubmit(): void {
    this.mensajeError = '';

    if (!this.nombre.trim()) {
      this.mensajeError = 'El nombre del hábito es obligatorio 📝';
      return;
    }

    if (!this.emoji) {
      this.mensajeError = 'Selecciona un emoji para tu hábito 🌱';
      return;
    }

    if (this.frecuencia === 'weekly' && this.targetDays.size === 0) {
      this.mensajeError = 'Selecciona al menos un día de la semana 📅';
      return;
    }

    this.cargando = true;

    const datos: any = {
      name: this.nombre.trim(),
      description: this.descripcion.trim(),
      emoji: this.emoji,
      frequency: this.frecuencia,
      targetDays: Array.from(this.targetDays).sort(),
      color: this.color
    };

    if (this.modoEdicion) {
      this.guardar.emit(datos as HabitUpdate);
    } else {
      this.guardar.emit(datos as HabitCreate);
    }
  }

  /**
   * Emite el evento de cancelar.
   */
  onCancelar(): void {
    this.cancelar.emit();
  }
}
