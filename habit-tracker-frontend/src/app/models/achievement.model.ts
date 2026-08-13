/**
 * Representa un logro que la app puede otorgar al usuario.
 * Se corresponde con Achievement del backend.
 */
export interface Achievement {
  id: number;
  code: string;
  name: string;
  description: string;
  emoji: string;
  conditionType: string;
  conditionValue: number;
  unlocked: boolean;
  unlockedAt: string | null;
}
