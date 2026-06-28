package com.habittracker.dao;

import com.habittracker.model.User;
import com.habittracker.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

/**
 * Data Access Object para la tabla 'users'.
 * Proporciona metodos para acceder y manipular los datos de usuarios en la base de datos.
 */
public class UserDAO {

    /**
     * Busca un usuario por su email.
     *
     * @param email El email del usuario a buscar
     * @return El objeto User si existe, null si no se encuentra
     */
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, email);

            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por email: " + e.getMessage());
        }

        return null;
    }

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param usuario El objeto User con los datos a guardar
     * @return El mismo usuario con el id asignado por la base de datos, o null si falla
     */
    public User save(User usuario) {
        String sql = "INSERT INTO users (name, email, password_hash, avatar_emoji) "
                   + "VALUES (?, ?, ?, ?)";

        try (Connection conexion = DatabaseConnection.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, usuario.getName());
            sentencia.setString(2, usuario.getEmail());
            sentencia.setString(3, usuario.getPasswordHash());
            sentencia.setString(4, usuario.getAvatarEmoji());

            int filasAfectadas = sentencia.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet clavesGeneradas = sentencia.getGeneratedKeys()) {
                    if (clavesGeneradas.next()) {
                        usuario.setId(clavesGeneradas.getInt(1));
                    }
                }
                usuario.setCreatedAt(new Timestamp(System.currentTimeMillis()));
                return usuario;
            }
        } catch (SQLException e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
        }

        return null;
    }

    /**
     * Convierte una fila del ResultSet en un objeto User.
     *
     * @param resultado El ResultSet apuntando a la fila actual
     * @return Un objeto User con los datos de la fila
     * @throws SQLException Si hay un error al leer el ResultSet
     */
    private User mapearUsuario(ResultSet resultado) throws SQLException {
        User usuario = new User();
        usuario.setId(resultado.getInt("id"));
        usuario.setName(resultado.getString("name"));
        usuario.setEmail(resultado.getString("email"));
        usuario.setPasswordHash(resultado.getString("password_hash"));
        usuario.setAvatarEmoji(resultado.getString("avatar_emoji"));
        usuario.setCreatedAt(resultado.getTimestamp("created_at"));
        return usuario;
    }
}
