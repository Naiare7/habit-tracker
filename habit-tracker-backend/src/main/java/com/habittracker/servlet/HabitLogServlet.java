package com.habittracker.servlet;

import com.google.gson.JsonSyntaxException;
import com.habittracker.dto.HabitLogDTO;
import com.habittracker.service.HabitLogService;
import com.habittracker.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Servlet que maneja el registro diario de habitos (marcar como hecho / desmarcar).
 * <p>
 * Rutas:
 * - POST /api/habit-logs → Marca o desmarca un habito en una fecha concreta
 * - GET  /api/habit-logs?userId=X&date=YYYY-MM-DD → Obtiene los registros de un dia
 * <p>
 * Todas las respuestas se devuelven en formato JSON.
 */
@WebServlet("/api/habit-logs")
public class HabitLogServlet extends HttpServlet {

    private final HabitLogService habitLogService;

    public HabitLogServlet() {
        this.habitLogService = new HabitLogService();
    }

    /**
     * Maneja las peticiones POST para marcar o desmarcar un habito en una fecha.
     * Lee el cuerpo JSON con habitId, userId, date y completed.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            HabitLogDTO dto = JsonUtils.leerCuerpo(request, HabitLogDTO.class);

            if (dto.getHabitId() <= 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El habitId es obligatorio"));
                return;
            }

            if (dto.getDate() == null || dto.getDate().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("La fecha es obligatoria"));
                return;
            }

            LocalDate fecha = LocalDate.parse(dto.getDate());

            HabitLogDTO resultado = habitLogService.markHabit(
                dto.getHabitId(), dto.getUserId(), fecha, dto.isCompleted());

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(resultado));

        } catch (JsonSyntaxException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("Formato JSON invalido"));
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("Formato de fecha invalido. Use YYYY-MM-DD"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtils.errorJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en HabitLogServlet doPost: " + e.getMessage());
        }
    }

    /**
     * Maneja las peticiones GET para obtener los registros de un usuario en un dia.
     * Lee los parametros "userId" y "date" de la URL.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String userIdParam = request.getParameter("userId");
            String dateParam = request.getParameter("date");

            if (userIdParam == null || userIdParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El parametro userId es obligatorio"));
                return;
            }

            if (dateParam == null || dateParam.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("El parametro date es obligatorio"));
                return;
            }

            int userId = Integer.parseInt(userIdParam);
            LocalDate fecha = LocalDate.parse(dateParam);

            List<HabitLogDTO> registros = habitLogService.getLogsForDay(userId, fecha);

            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(JsonUtils.aJson(registros));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El parametro userId debe ser un numero valido"));
        } catch (DateTimeParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("Formato de fecha invalido. Use YYYY-MM-DD"));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en HabitLogServlet doGet: " + e.getMessage());
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
