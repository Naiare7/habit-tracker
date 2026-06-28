package com.habittracker.servlet;

import com.google.gson.JsonSyntaxException;
import com.habittracker.dto.HabitCreateDTO;
import com.habittracker.dto.HabitResponseDTO;
import com.habittracker.dto.HabitUpdateDTO;
import com.habittracker.service.HabitService;
import com.habittracker.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet que maneja la gestion de habitos.
 * <p>
 * Rutas:
 * - GET    /api/habits?userId=X → Lista los habitos de un usuario
 * - POST   /api/habits           → Crea un nuevo habito
 * - PUT    /api/habits/{id}      → Actualiza un habito existente
 * - DELETE /api/habits/{id}      → Elimina un habito (borrado logico)
 * <p>
 * Todas las respuestas se devuelven en formato JSON.
 */
@WebServlet("/api/habits/*")
public class HabitServlet extends HttpServlet {

    private final HabitService habitService;

    public HabitServlet() {
        this.habitService = new HabitService();
    }

    /**
     * Maneja las peticiones GET para listar los habitos de un usuario.
     * Lee el parametro "userId" de la URL.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String userIdParam = request.getParameter("userId");

            if (userIdParam == null || userIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El parametro userId es obligatorio"));
                return;
            }

            int userId = Integer.parseInt(userIdParam);
            List<HabitResponseDTO> habitos = habitService.getHabitsByUser(userId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(habitos));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El parametro userId debe ser un numero valido"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en HabitServlet doGet: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones POST para crear un nuevo habito.
     * Lee el cuerpo JSON como HabitCreateDTO y llama al servicio.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HabitCreateDTO dto = JsonUtils.leerCuerpo(request, HabitCreateDTO.class);
            HabitResponseDTO resultado = habitService.createHabit(dto);

            response.setStatus(HttpServletResponse.SC_CREATED);
            response.getWriter().write(JsonUtils.aJson(resultado));

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
            System.err.println("Error en HabitServlet doPost: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones PUT para actualizar un habito existente.
     * Lee el id del habito de la URL y el cuerpo JSON como HabitUpdateDTO.
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("Especifica el id del habito en la URL"));
                return;
            }

            String habitIdStr = pathInfo.substring(1);
            int habitId = Integer.parseInt(habitIdStr);

            HabitUpdateDTO dto = JsonUtils.leerCuerpo(request, HabitUpdateDTO.class);
            dto.setId(habitId);

            HabitResponseDTO resultado = habitService.updateHabit(dto);

            if (resultado == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(
                    JsonUtils.errorJson("Habito no encontrado"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(resultado));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El id del habito debe ser un numero valido"));
        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("Formato JSON invalido"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en HabitServlet doPut: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones DELETE para eliminar un habito (borrado logico).
     * Lee el id del habito de la URL.
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("Especifica el id del habito en la URL"));
                return;
            }

            String habitIdStr = pathInfo.substring(1);
            int habitId = Integer.parseInt(habitIdStr);

            boolean eliminado = habitService.deleteHabit(habitId);

            if (!eliminado) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(
                    JsonUtils.errorJson("Habito no encontrado"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(
                new java.util.HashMap<String, Object>() {{
                    put("mensaje", "Habito eliminado correctamente");
                }}
            ));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El id del habito debe ser un numero valido"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en HabitServlet doDelete: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones OPTIONS para CORS preflight.
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCors(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    /**
     * Configura las cabeceras CORS para permitir peticiones desde el frontend Angular.
     */
    private void configurarCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods",
            "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers",
            "Content-Type, Authorization");
    }
}
