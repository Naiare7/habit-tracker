/**
 * Representa el registro diario de un habito (completado o no).
 * Se corresponde con HabitLogDTO del backend.
 */
export interface HabitLog {
  habitId: number;
  userId: number;
  date: string;
  completed: boolean;
  notes?: string;
}
