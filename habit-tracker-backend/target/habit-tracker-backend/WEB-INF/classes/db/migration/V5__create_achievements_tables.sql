-- Tabla que almacena los logros predefinidos que la app puede otorgar
-- Cada logro tiene un codigo unico, un nombre visible, una condicion a evaluar
-- y un emoji para mostrarlo en la pantalla de logros
CREATE TABLE achievements (
    id SERIAL PRIMARY KEY,                            -- Identificador unico del logro
    code VARCHAR(50) NOT NULL UNIQUE,                 -- Codigo identificador interno (ej: 'streak_7')
    name VARCHAR(100) NOT NULL,                       -- Nombre visible del logro
    description TEXT,                                 -- Descripcion del logro para el usuario
    emoji VARCHAR(10) NOT NULL DEFAULT '🌟',          -- Emoji que representa el logro
    condition_type VARCHAR(50) NOT NULL,              -- Tipo de condicion a evaluar (ej: 'streak_days', 'perfect_week', 'perfect_month')
    condition_value INTEGER NOT NULL                  -- Valor que debe alcanzar la condicion (ej: 7, 30, 1)
);

-- Tabla que guarda que logros ha desbloqueado cada usuario
-- Cada fila representa un logro otorgado a un usuario concreto
CREATE TABLE user_achievements (
    id SERIAL PRIMARY KEY,                            -- Identificador unico del registro
    user_id INTEGER NOT NULL,                         -- ID del usuario que desbloqueo el logro (FK -> users)
    achievement_id INTEGER NOT NULL,                  -- ID del logro desbloqueado (FK -> achievements)
    unlocked_at TIMESTAMP DEFAULT NOW(),              -- Fecha y hora en que se desbloqueo el logro
    UNIQUE(user_id, achievement_id),                  -- Evita que un usuario desbloquee el mismo logro dos veces
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (achievement_id) REFERENCES achievements(id) ON DELETE CASCADE
);

-- Logros predefinidos que se otorgan automaticamente al cumplir su condicion
INSERT INTO achievements (code, name, description, emoji, condition_type, condition_value) VALUES
('perfect_week', 'Semana perfecta', 'Completa todos tus habitos diarios durante 7 dias seguidos', '🏆', 'perfect_week', 7),
('streak_7', 'Racha de 7 dias', 'Completa un habito 7 dias consecutivos', '🌟', 'streak_days', 7),
('streak_30', 'Racha de 30 dias', 'Completa un habito 30 dias consecutivos', '🔥', 'streak_days', 30),
('perfect_month', 'Mes perfecto', 'Completa todos tus habitos todos los dias de un mes', '🎖️', 'perfect_month', 1);
