-- Tabla que almacena los recordatorios (alarmas) configurados por el usuario para cada habito
-- Cada recordatorio indica a que hora y que dias de la semana debe avisar la app
CREATE TABLE reminders (
    id SERIAL PRIMARY KEY,                        -- Identificador unico del recordatorio
    habit_id INTEGER NOT NULL,                    -- ID del habito al que pertenece (FK -> habits)
    user_id INTEGER NOT NULL,                     -- ID del usuario propietario (FK -> users)
    reminder_time TIME NOT NULL,                  -- Hora del recordatorio (ej: '08:30:00')
    days_of_week INTEGER[],                       -- Array de dias de la semana (0=domingo..6=sabado)
    is_active BOOLEAN DEFAULT TRUE,               -- Indica si el recordatorio esta activo (toggle on/off)
    created_at TIMESTAMP DEFAULT NOW(),           -- Fecha y hora de creacion del recordatorio
    FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
