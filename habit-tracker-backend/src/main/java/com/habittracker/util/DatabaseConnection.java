package com.habittracker.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Gestiona la conexion a la base de datos PostgreSQL.
 * <p>
 * Lee la configuracion desde el archivo db.properties ubicado en resources
 * y proporciona una conexion JDBC mediante el metodo estatico getConnection().
 */
public class DatabaseConnection {

    private static String url;
    private static String username;
    private static String password;

    // Carga las propiedades y registra el driver JDBC al iniciar la clase
    static {
        cargarPropiedades();
        registrarDriver();
    }

    /**
     * Registra explicitamente el driver de PostgreSQL.
     * Necesario en entornos Tomcat donde la carga automatica via ServiceLoader
     * puede no funcionar correctamente.
     */
    private static void registrarDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se encontro el driver JDBC de PostgreSQL", e);
        }
    }

    /**
     * Carga la configuracion de la base de datos desde db.properties.
     * El archivo debe estar en el classpath (src/main/resources/).
     */
    private static void cargarPropiedades() {
        Properties properties = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader()
                .getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException(
                    "No se encontro el archivo db.properties en el classpath");
            }

            properties.load(input);
            url = properties.getProperty("db.url");
            username = properties.getProperty("db.username");
            password = properties.getProperty("db.password");

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar db.properties", e);
        }
    }

    /**
     * Obtiene una conexion a la base de datos PostgreSQL.
     *
     * @return Un objeto Connection listo para usar
     * @throws SQLException Si ocurre un error al conectar
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }
}
