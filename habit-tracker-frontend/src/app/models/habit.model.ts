/**
 * Representa un habito con todos sus campos.
 * Se corresponde con HabitResponseDTO del backend.
 */
export interface Habit {
  id: number;
  userId: number;
  name: string;
  description: string;
  emoji: string;
  frequency: string;
  targetDays: number[];
  color: string;
  isActive: boolean;
  createdAt: string;
}

/**
 * Datos necesarios para crear un nuevo habito.
 * Se corresponde con HabitCreateDTO del backend.
 */
export interface HabitCreate {
  userId: number;
  name: string;
  description: string;
  emoji: string;
  frequency: string;
  targetDays: number[];
  color: string;
}

/**
 * Datos necesarios para actualizar un habito existente.
 * Se corresponde con HabitUpdateDTO del backend.
 */
export interface HabitUpdate {
  name: string;
  description: string;
  emoji: string;
  frequency: string;
  targetDays: number[];
  color: string;
}
