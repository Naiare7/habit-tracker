package com.habittracker.servlet;

import com.google.gson.JsonSyntaxException;
import com.habittracker.dto.UserLoginDTO;
import com.habittracker.dto.UserRegisterDTO;
import com.habittracker.dto.UserResponseDTO;
import com.habittracker.service.UserService;
import com.habittracker.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet que maneja la autenticacion de usuarios.
 * <p>
 * Rutas:
 * - POST /api/auth/register → Registra un nuevo usuario
 * - POST /api/auth/login    → Inicia sesion y devuelve un token
 * <p>
 * Todas las respuestas se devuelven en formato JSON.
 */
@WebServlet("/api/auth/*")
public class AuthServlet extends HttpServlet {

    private final UserService userService;

    public AuthServlet() {
        this.userService = new UserService();
    }

    /**
     * Maneja las peticiones POST para registro e inicio de sesion.
     * Lee la URL para determinar si es /register o /login.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar CORS y tipo de contenido
        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Obtener la ruta relativa para saber que accion ejecutar
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("Especifica /register o /login"));
                return;
            }

            switch (pathInfo) {
                case "/register":
                    manejarRegistro(request, response);
                    break;
                case "/login":
                    manejarLogin(request, response);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write(
                        JsonUtils.errorJson("Ruta no encontrada: " + pathInfo));
            }

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("Formato JSON invalido"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtils.errorJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en AuthServlet: " + e.getMessage());
        }
    }

    /**
     * Procesa el registro de un nuevo usuario.
     * Lee el cuerpo JSON como UserRegisterDTO y llama al servicio.
     */
    private void manejarRegistro(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        UserRegisterDTO dto = JsonUtils.leerCuerpo(request, UserRegisterDTO.class);
        UserResponseDTO resultado = userService.register(dto);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(JsonUtils.aJson(resultado));
    }

    /**
     * Procesa el inicio de sesion.
     * Lee el cuerpo JSON como UserLoginDTO, llama al servicio y genera un token UUID.
     */
    private void manejarLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        UserLoginDTO dto = JsonUtils.leerCuerpo(request, UserLoginDTO.class);
        UserResponseDTO resultado = userService.login(dto);

        // Generar un token de sesion simple (UUID)
        resultado.setToken(UUID.randomUUID().toString());

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(resultado));
    }

    /**
     * Configura las cabeceras CORS para permitir peticiones desde el frontend Angular.
     */
    private void configurarCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
