-- Tabla que almacena el registro diario de habitos completados por cada usuario
-- Cada fila representa si un habito fue completado o no en una fecha concreta
CREATE TABLE habit_logs (
    id SERIAL PRIMARY KEY,                                    -- Identificador unico del registro
    habit_id INTEGER NOT NULL,                                -- ID del habito (FK -> habits)
    user_id INTEGER NOT NULL,                                 -- ID del usuario (FK -> users)
    completed_date DATE NOT NULL,                             -- Fecha del registro (sin hora)
    completed BOOLEAN NOT NULL DEFAULT FALSE,                 -- Indica si el habito fue completado ese dia
    notes TEXT,                                               -- Notas opcionales del usuario sobre ese dia
    created_at TIMESTAMP DEFAULT NOW(),                       -- Fecha y hora de creacion del registro
    UNIQUE(habit_id, completed_date),                         -- Evita duplicados del mismo habito en la misma fecha
    FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indice para consultar rapidamente los registros de un usuario en un rango de fechas
-- Util en las consultas del dashboard y estadisticas
CREATE INDEX idx_habit_logs_user_date ON habit_logs(user_id, completed_date);
