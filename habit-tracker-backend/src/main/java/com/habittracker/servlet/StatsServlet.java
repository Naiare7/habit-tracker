package com.habittracker.servlet;

import com.habittracker.dto.DailyStatsDTO;
import com.habittracker.dto.MonthlyStatsDTO;
import com.habittracker.dto.StreakDTO;
import com.habittracker.dto.WeeklyStatsEntryDTO;
import com.habittracker.service.StatsService;
import com.habittracker.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet que devuelve las estadisticas de progreso del usuario.
 * <p>
 * Rutas:
 * - GET /api/stats/weekly?userId=X
 *   → Lista de habitos completados por dia de la semana actual
 * - GET /api/stats/monthly?userId=X&year=YYYY&month=MM
 *   → Porcentaje de cumplimiento del mes indicado
 * - GET /api/stats/streak?habitId=X
 *   → Racha de dias consecutivos del habito indicado
 * - GET /api/stats/calendar?userId=X&year=YYYY&month=MM
 *   → Nivel de cumplimiento de cada dia del mes indicado
 * <p>
 * Todas las respuestas se devuelven en formato JSON.
 */
@WebServlet("/api/stats/*")
public class StatsServlet extends HttpServlet {

    private final StatsService statsService;

    public StatsServlet() {
        this.statsService = new StatsService();
    }

    /**
     * Maneja las peticiones GET de las estadisticas.
     * Lee la URL para saber que estadistica devolver (semanal, mensual o racha).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar CORS y tipo de contenido
        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Obtener la ruta relativa para saber que estadistica consultar
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(
                    JsonUtils.errorJson("Especifica /weekly, /monthly, /streak o /calendar"));
                return;
            }

            switch (pathInfo) {
                case "/weekly":
                    manejarEstadisticasSemanales(request, response);
                    break;
                case "/monthly":
                    manejarEstadisticasMensuales(request, response);
                    break;
                case "/streak":
                    manejarRacha(request, response);
                    break;
                case "/calendar":
                    manejarCalendario(request, response);
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write(
                        JsonUtils.errorJson("Ruta no encontrada: " + pathInfo));
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("Los parametros numericos son invalidos"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtils.errorJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en StatsServlet: " + e.getMessage());
        }
    }

    /**
     * Devuelve cuantos habitos se completaron cada dia de la semana actual.
     * Lee el parametro obligatorio "userId" de la URL.
     */
    private void manejarEstadisticasSemanales(HttpServletRequest request,
                                              HttpServletResponse response)
            throws IOException {

        String userIdParam = request.getParameter("userId");
        validarParametroObligatorio(response, userIdParam, "userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        List<WeeklyStatsEntryDTO> resultado = statsService.getWeeklyStats(userId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(resultado));
    }

    /**
     * Devuelve el porcentaje de cumplimiento de un mes concreto.
     * Lee los parametros obligatorios "userId", "year" y "month" de la URL.
     */
    private void manejarEstadisticasMensuales(HttpServletRequest request,
                                              HttpServletResponse response)
            throws IOException {

        String userIdParam = request.getParameter("userId");
        String yearParam = request.getParameter("year");
        String monthParam = request.getParameter("month");

        validarParametroObligatorio(response, userIdParam, "userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            return;
        }

        validarParametroObligatorio(response, yearParam, "year");
        if (yearParam == null || yearParam.isEmpty()) {
            return;
        }

        validarParametroObligatorio(response, monthParam, "month");
        if (monthParam == null || monthParam.isEmpty()) {
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        int year = Integer.parseInt(yearParam);
        int month = Integer.parseInt(monthParam);

        MonthlyStatsDTO resultado = statsService.getMonthlyStats(userId, year, month);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(resultado));
    }

    /**
     * Devuelve la racha de dias consecutivos de un habito.
     * Lee el parametro obligatorio "habitId" de la URL.
     */
    private void manejarRacha(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String habitIdParam = request.getParameter("habitId");
        validarParametroObligatorio(response, habitIdParam, "habitId");
        if (habitIdParam == null || habitIdParam.isEmpty()) {
            return;
        }

        int habitId = Integer.parseInt(habitIdParam);
        StreakDTO resultado = statsService.getStreak(habitId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(resultado));
    }

    /**
     * Devuelve el nivel de cumplimiento de cada dia de un mes concreto.
     * Lee los parametros obligatorios "userId", "year" y "month" de la URL.
     */
    private void manejarCalendario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String userIdParam = request.getParameter("userId");
        String yearParam = request.getParameter("year");
        String monthParam = request.getParameter("month");

        validarParametroObligatorio(response, userIdParam, "userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            return;
        }

        validarParametroObligatorio(response, yearParam, "year");
        if (yearParam == null || yearParam.isEmpty()) {
            return;
        }

        validarParametroObligatorio(response, monthParam, "month");
        if (monthParam == null || monthParam.isEmpty()) {
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        int year = Integer.parseInt(yearParam);
        int month = Integer.parseInt(monthParam);

        List<DailyStatsDTO> resultado = statsService.getDailyStats(userId, year, month);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(resultado));
    }

    /**
     * Comprueba que un parametro de la URL este presente.
     * Si no lo esta, devuelve un error 400 con el nombre del parametro faltante.
     *
     * @param response    La respuesta HTTP para escribir el error
     * @param valorParam  El valor del parametro recibido (puede ser null)
     * @param nombreParam El nombre del parametro a validar
     * @throws IOException Si hay un error al escribir la respuesta
     */
    private void validarParametroObligatorio(HttpServletResponse response,
                                             String valorParam,
                                             String nombreParam)
            throws IOException {

        if (valorParam == null || valorParam.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El parametro " + nombreParam + " es obligatorio"));
        }
    }

    /**
     * Configura las cabeceras CORS para permitir peticiones desde el frontend Angular.
     */
    private void configurarCors(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
