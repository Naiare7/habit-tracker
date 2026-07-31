package com.habittracker.servlet;

import com.google.gson.JsonSyntaxException;
import com.habittracker.dto.ReminderDTO;
import com.habittracker.service.ReminderService;
import com.habittracker.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet que maneja la gestion de recordatorios de habitos.
 * <p>
 * Rutas:
 * - GET    /api/reminders?habitId=X → Lista los recordatorios de un habito
 * - POST   /api/reminders           → Crea un nuevo recordatorio
 * - PUT    /api/reminders/{id}      → Actualiza un recordatorio existente
 * - DELETE /api/reminders/{id}      → Elimina un recordatorio
 * <p>
 * Todas las respuestas se devuelven en formato JSON.
 */
@WebServlet("/api/reminders/*")
public class ReminderServlet extends HttpServlet {

    private final ReminderService reminderService;

    public ReminderServlet() {
        this.reminderService = new ReminderService();
    }

    /**
     * Maneja las peticiones GET para listar los recordatorios de un habito.
     * Lee el parametro "habitId" de la URL.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String habitIdParam = request.getParameter("habitId");

            if (habitIdParam == null || habitIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El parametro habitId es obligatorio"));
                return;
            }

            int habitId = Integer.parseInt(habitIdParam);
            List<ReminderDTO> recordatorios = reminderService.getRemindersByHabit(habitId);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(recordatorios));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El parametro habitId debe ser un numero valido"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en ReminderServlet doGet: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones POST para crear un nuevo recordatorio.
     * Lee el cuerpo JSON como ReminderDTO y llama al servicio.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ReminderDTO dto = JsonUtils.leerCuerpo(request, ReminderDTO.class);

            if (dto.getHabitId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El habitId es obligatorio"));
                return;
            }

            if (dto.getUserId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El userId es obligatorio"));
                return;
            }

            if (dto.getReminderTime() == null || dto.getReminderTime().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("La hora del recordatorio es obligatoria"));
                return;
            }

            ReminderDTO resultado = reminderService.createReminder(dto);

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
            System.err.println("Error en ReminderServlet doPost: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones PUT para actualizar un recordatorio existente.
     * Lee el id del recordatorio de la URL y el cuerpo JSON como ReminderDTO.
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
                    JsonUtils.errorJson("Especifica el id del recordatorio en la URL"));
                return;
            }

            String reminderIdStr = pathInfo.substring(1);
            int reminderId = Integer.parseInt(reminderIdStr);

            ReminderDTO dto = JsonUtils.leerCuerpo(request, ReminderDTO.class);
            dto.setId(reminderId);

            if (dto.getUserId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El userId es obligatorio"));
                return;
            }

            if (dto.getReminderTime() == null || dto.getReminderTime().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("La hora del recordatorio es obligatoria"));
                return;
            }

            ReminderDTO resultado = reminderService.updateReminder(dto);

            if (resultado == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(
                    JsonUtils.errorJson("Recordatorio no encontrado"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(resultado));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El id del recordatorio debe ser un numero valido"));
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
            System.err.println("Error en ReminderServlet doPut: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones DELETE para eliminar un recordatorio.
     * Lee el id del recordatorio de la URL.
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
                    JsonUtils.errorJson("Especifica el id del recordatorio en la URL"));
                return;
            }

            String reminderIdStr = pathInfo.substring(1);
            int reminderId = Integer.parseInt(reminderIdStr);

            boolean eliminado = reminderService.deleteReminder(reminderId);

            if (!eliminado) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(
                    JsonUtils.errorJson("Recordatorio no encontrado"));
                return;
            }

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(
                new java.util.HashMap<String, Object>() {{
                    put("mensaje", "Recordatorio eliminado correctamente");
                }}
            ));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El id del recordatorio debe ser un numero valido"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en ReminderServlet doDelete: " + e.getMessage());
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
