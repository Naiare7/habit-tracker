-- Tabla que almacena los usuarios registrados en la aplicacion
-- Cada usuario tiene un nombre, email unico, contrasena encriptada,
-- un emoji de avatar para personalizar su perfil, y la fecha de creacion.
CREATE TABLE users (
    id SERIAL PRIMARY KEY,                                 -- Identificador unico del usuario
    name VARCHAR(100) NOT NULL,                            -- Nombre completo del usuario
    email VARCHAR(255) NOT NULL UNIQUE,                    -- Email del usuario (debe ser unico en el sistema)
    password_hash VARCHAR(255) NOT NULL,                   -- Hash de la contrasena (encriptado con BCrypt)
    avatar_emoji VARCHAR(10) DEFAULT '🌸',                 -- Emoji que representa al usuario (opcional)
    created_at TIMESTAMP DEFAULT NOW()                     -- Fecha y hora en que se registro el usuario
);
