/**
 * Representa un recordatorio (alarma) de un habito con todos sus campos.
 * Se corresponde con ReminderDTO del backend.
 */
export interface Reminder {
  id: number;
  habitId: number;
  userId: number;
  reminderTime: string;
  daysOfWeek: number[];
  isActive: boolean;
}

/**
 * Datos necesarios para crear un nuevo recordatorio.
 * Se corresponde con el cuerpo del POST /api/reminders.
 */
export interface ReminderCreate {
  habitId: number;
  userId: number;
  reminderTime: string;
  daysOfWeek: number[];
}

/**
 * Datos necesarios para actualizar un recordatorio existente.
 * Se corresponde con el cuerpo del PUT /api/reminders/{id}.
 */
export interface ReminderUpdate {
  id: number;
  userId: number;
  reminderTime: string;
  daysOfWeek: number[];
  isActive: boolean;
}
