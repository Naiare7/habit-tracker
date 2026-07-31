/**
 * Representa un dia de la semana con el numero de habitos completados.
 * Se corresponde con WeeklyStatsEntryDTO del backend.
 */
export interface WeeklyStatsEntry {
  dayName: string;
  completedCount: number;
  date: string;
}

/**
 * Representa el porcentaje de cumplimiento de un mes.
 * Se corresponde con MonthlyStatsDTO del backend.
 */
export interface MonthlyStats {
  percentage: number;
  completedDays: number;
  totalDaysInMonth: number;
}

/**
 * Representa la racha de dias consecutivos de un habito.
 * Se corresponde con StreakDTO del backend.
 */
export interface Streak {
  habitId: number;
  streak: number;
}

/**
 * Representa el nivel de cumplimiento de un dia del mes.
 * Se corresponde con DailyStatsDTO del backend.
 */
export interface DailyStats {
  date: string;
  completedCount: number;
  totalHabits: number;
  percentage: number;
}
