-- Tabla que almacena los habitos creados por cada usuario
-- Cada habito pertenece a un usuario y tiene configuracion de frecuencia y apariencia
CREATE TABLE habits (
    id SERIAL PRIMARY KEY,                                  -- Identificador unico del habito
    user_id INTEGER NOT NULL,                               -- ID del usuario propietario (FK -> users)
    name VARCHAR(100) NOT NULL,                             -- Nombre del habito (ej: "Beber agua", "Meditar")
    description TEXT,                                       -- Descripcion opcional del habito
    emoji VARCHAR(10) NOT NULL DEFAULT '🌟',                -- Emoji representativo del habito
    frequency VARCHAR(10) NOT NULL DEFAULT 'daily',         -- Frecuencia: 'daily' (diario) o 'weekly' (semanal)
    target_days INTEGER[],                                  -- Array de dias para frecuencia semanal (0=domingo..6=sabado)
    color VARCHAR(7) DEFAULT '#FFB347',                     -- Color hexadecimal para la tarjeta del habito
    is_active BOOLEAN DEFAULT TRUE,                         -- Indica si el habito esta activo (borrado logico)
    created_at TIMESTAMP DEFAULT NOW(),                     -- Fecha y hora de creacion del habito
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
