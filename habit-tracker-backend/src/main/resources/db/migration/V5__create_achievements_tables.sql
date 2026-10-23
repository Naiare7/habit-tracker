-- Tarea 7.1: Sistema de logros y celebraciones
-- Tabla que almacena los logros/medallas disponibles en la aplicación
CREATE TABLE achievements (
  id SERIAL PRIMARY KEY,
  code VARCHAR(50) UNIQUE NOT NULL,        -- Código único del logro (ej: "streak_7")
  name VARCHAR(100) NOT NULL,              -- Nombre del logro
  description TEXT NOT NULL,               -- Descripción de cómo obtenerlo
  emoji VARCHAR(10) NOT NULL,              -- Emoji representativo
  condition_type VARCHAR(50) NOT NULL,     -- Tipo de condición (ej: "streak", "first_week", "perfect_month")
  condition_value INTEGER NOT NULL,        -- Valor objetivo para desbloquear el logro
  created_at TIMESTAMP DEFAULT NOW()
);

-- Tabla que relaciona usuarios con logros desbloqueados
CREATE TABLE user_achievements (
  id SERIAL PRIMARY KEY,
  user_id INTEGER NOT NULL,
  achievement_id INTEGER NOT NULL,
  unlocked_at TIMESTAMP DEFAULT NOW(),
  CONSTRAINT fk_user_achievement_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_user_achievement_achievement FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE,
  CONSTRAINT unique_user_achievement UNIQUE (user_id, achievement_id)  -- Un usuario solo puede desbloquear cada logro una vez
);

-- Logros predefinidos
INSERT INTO achievements (code, name, description, emoji, condition_type, condition_value) VALUES
('first_week', 'Primera semana', '¡Completaste tu primera semana con hábitos!', '🌱', 'first_week', 1),
('streak_7', 'Racha de 7 días', 'Mantuviste una racha de 7 días seguidos', '🔥', 'streak', 7),
('streak_30', 'Racha de 30 días', 'Mantuviste una racha de 30 días seguidos', '🏆', 'streak', 30),
('perfect_month', 'Mes perfecto', 'Completaste todos los hábitos todos los días del mes', '✨', 'perfect_month', 1);
