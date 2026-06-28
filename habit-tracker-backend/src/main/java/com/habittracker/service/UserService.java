package com.habittracker.service;

import com.habittracker.dao.UserDAO;
import com.habittracker.dto.UserLoginDTO;
import com.habittracker.dto.UserRegisterDTO;
import com.habittracker.dto.UserResponseDTO;
import com.habittracker.model.User;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Servicio con la logica de negocio para la gestion de usuarios.
 * Se encarga del registro (encriptando la contrasena) y del inicio de sesion.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Encripta la contrasena con BCrypt antes de guardarla.
     *
     * @param dto Datos del formulario de registro (name, email, password)
     * @return UserResponseDTO con los datos del usuario creado
     * @throws IllegalArgumentException Si el email ya esta registrado
     */
    public UserResponseDTO register(UserRegisterDTO dto) {
        // Verificar si el email ya existe
        User existente = userDAO.findByEmail(dto.getEmail());
        if (existente != null) {
            throw new IllegalArgumentException("El email ya esta registrado");
        }

        // Encriptar la contrasena con BCrypt
        String hash = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());

        // Crear el usuario y guardarlo
        User nuevoUsuario = new User();
        nuevoUsuario.setName(dto.getName());
        nuevoUsuario.setEmail(dto.getEmail());
        nuevoUsuario.setPasswordHash(hash);
        nuevoUsuario.setAvatarEmoji("🌸");

        User usuarioGuardado = userDAO.save(nuevoUsuario);

        if (usuarioGuardado == null) {
            throw new RuntimeException("Error al guardar el usuario en la base de datos");
        }

        return new UserResponseDTO(usuarioGuardado);
    }

    /**
     * Inicia sesion con email y contrasena.
     * Verifica que la contrasena coincida con el hash almacenado.
     *
     * @param dto Credenciales de inicio de sesion (email, password)
     * @return UserResponseDTO con los datos del usuario
     * @throws IllegalArgumentException Si el email no existe o la contrasena es incorrecta
     */
    public UserResponseDTO login(UserLoginDTO dto) {
        // Buscar el usuario por email
        User usuario = userDAO.findByEmail(dto.getEmail());
        if (usuario == null) {
            throw new IllegalArgumentException("Email o contrasena incorrectos");
        }

        // Verificar la contrasena contra el hash almacenado
        if (!BCrypt.checkpw(dto.getPassword(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("Email o contrasena incorrectos");
        }

        return new UserResponseDTO(usuario);
    }
}
