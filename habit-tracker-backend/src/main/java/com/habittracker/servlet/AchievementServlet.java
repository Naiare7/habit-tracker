package com.habittracker.servlet;

import com.habittracker.model.Achievement;
import com.habittracker.service.AchievementService;
import com.habittracker.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Servlet que expone los logros de la aplicacion.
 * <p>
 * Rutas:
 * - GET /api/achievements?userId=X
 *   → Todos los logros con su estado de desbloqueo para el usuario
 * - GET /api/achievements/new
 *   → Solo los logros recien desbloqueados en esta sesion
 * <p>
 * Todas las respuestas se devuelven en formato JSON.
 */
@WebServlet("/api/achievements/*")
public class AchievementServlet extends HttpServlet {

    private final AchievementService achievementService;

    public AchievementServlet() {
        this.achievementService = new AchievementService();
    }

    /**
     * Maneja las peticiones GET de los logros.
     * Lee la URL para saber si devolver todos los logros o solo los nuevos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Configurar CORS y tipo de contenido
        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            // Obtener la ruta relativa para saber que consulta realizar
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                // GET /api/achievements: todos los logros con su estado
                manejarTodosLosLogros(request, response);
            } else if (pathInfo.equals("/new")) {
                // GET /api/achievements/new: logros recien desbloqueados
                manejarLogrosNuevos(response);
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write(
                    JsonUtils.errorJson("Ruta no encontrada: " + pathInfo));
            }

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El parametro userId debe ser un numero valido"));
        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(JsonUtils.errorJson(e.getMessage()));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(
                JsonUtils.errorJson("Error interno del servidor"));
            System.err.println("Error en AchievementServlet: " + e.getMessage());
        }
    }

    /**
     * Devuelve todos los logros con su estado de desbloqueo.
     * Lee el parametro obligatorio "userId" de la URL.
     */
    private void manejarTodosLosLogros(HttpServletRequest request,
                                       HttpServletResponse response)
            throws IOException {

        String userIdParam = request.getParameter("userId");
        validarParametroObligatorio(response, userIdParam, "userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        List<Achievement> logros = achievementService.getAllWithStatus(userId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(logros));
    }

    /**
     * Devuelve los logros recien desbloqueados en esta sesion.
     * La lista de sesion se vacia despues de la consulta.
     */
    private void manejarLogrosNuevos(HttpServletResponse response)
            throws IOException {

        List<Achievement> logros = AchievementService.getNewlyUnlocked();

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(logros));
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
        response.setHeader("Access-Control-Allow-Methods", "GET, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }
}
