package com.habittracker.servlet;

import com.habittracker.dao.AchievementDAO;
import com.habittracker.dto.AchievementWithStatusDTO;
import com.habittracker.model.Achievement;
import com.habittracker.service.AchievementService;
import com.habittracker.util.JsonUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Servlet que maneja los logros del usuario.
 * <p>
 * Rutas:
 * - GET /api/achievements?userId=X        → Devuelve todos los logros con su estado de desbloqueo
 * - GET /api/achievements/new?userId=X    → Devuelve solo los logros recién desbloqueados en esta sesión
 * <p>
 * Todas las respuestas se devuelven en formato JSON.
 */
@WebServlet("/api/achievements/*")
public class AchievementServlet extends HttpServlet {

    private final AchievementService achievementService;
    private final AchievementDAO achievementDAO;

    public AchievementServlet() {
        this.achievementService = new AchievementService();
        this.achievementDAO = new AchievementDAO();
    }

    /**
     * Maneja las peticiones GET para consultar logros.
     * Lee la URL para saber si devolver todos los logros o solo los nuevos.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        configurarCors(response);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = request.getPathInfo();

            if (pathInfo == null || pathInfo.equals("/")) {
                manejarTodosLosLogros(request, response);
                return;
            }

            if ("/new".equals(pathInfo)) {
                manejarLogrosNuevos(request, response);
                return;
            }

            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write(
                JsonUtils.errorJson("Ruta no encontrada: " + pathInfo));

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
     * Devuelve todos los logros disponibles con su estado de desbloqueo.
     * Lee el parametro obligatorio "userId" de la URL.
     */
    private void manejarTodosLosLogros(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String userIdParam = request.getParameter("userId");
        validarParametroObligatorio(response, userIdParam, "userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            return;
        }

        int userId = Integer.parseInt(userIdParam);

        List<Achievement> todosLosLogros = achievementDAO.findAll();
        List<Achievement> logrosDesbloqueados = achievementDAO.findUnlockedByUserId(userId);

        // Crea un set con los ids de logros desbloqueados para buscarlos rapido
        Set<Integer> idsDesbloqueados = new HashSet<>();
        for (Achievement logro : logrosDesbloqueados) {
            idsDesbloqueados.add(logro.getId());
        }

        List<AchievementWithStatusDTO> resultado = new ArrayList<>();
        for (Achievement logro : todosLosLogros) {
            boolean desbloqueado = idsDesbloqueados.contains(logro.getId());
            String fechaDesbloqueo = null;

            if (desbloqueado) {
                // Busca la fecha de desbloqueo en la lista de desbloqueados
                for (Achievement desbloqueadoItem : logrosDesbloqueados) {
                    if (desbloqueadoItem.getId() == logro.getId()) {
                        if (desbloqueadoItem.getCreatedAt() != null) {
                            fechaDesbloqueo = desbloqueadoItem.getCreatedAt().toString();
                        }
                        break;
                    }
                }
            }

            resultado.add(new AchievementWithStatusDTO(logro, desbloqueado, fechaDesbloqueo));
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(resultado));
    }

    /**
     * Devuelve solo los logros recién desbloqueados en esta sesión.
     * Evalua los logros antes de buscar los nuevos para asegurar que
     * se detecten los desbloqueados tras marcar un habito.
     * Lee el parametro obligatorio "userId" de la URL.
     */
    @SuppressWarnings("unchecked")
    private void manejarLogrosNuevos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String userIdParam = request.getParameter("userId");
        validarParametroObligatorio(response, userIdParam, "userId");
        if (userIdParam == null || userIdParam.isEmpty()) {
            return;
        }

        int userId = Integer.parseInt(userIdParam);
        HttpSession sesion = request.getSession(true);

        // Obtiene los ids de logros que ya se mostraron en esta sesion
        Set<Integer> idsYaVistos = (Set<Integer>) sesion.getAttribute("logrosVistos");
        if (idsYaVistos == null) {
            idsYaVistos = new HashSet<>();
        }

        // Evalua los logros por si se acaban de desbloquear
        achievementService.checkAndUnlockAchievements(userId);

        // Obtiene los logros desbloqueados actualmente
        List<Achievement> logrosDesbloqueadosActuales = achievementDAO.findUnlockedByUserId(userId);

        // Encuentra solo los que no se han mostrado antes en esta sesion
        List<Achievement> logrosNuevosSesion = new ArrayList<>();
        for (Achievement logro : logrosDesbloqueadosActuales) {
            if (!idsYaVistos.contains(logro.getId())) {
                logrosNuevosSesion.add(logro);
                idsYaVistos.add(logro.getId());
            }
        }

        // Actualiza la sesion con los ids ya vistos
        sesion.setAttribute("logrosVistos", idsYaVistos);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(JsonUtils.aJson(logrosNuevosSesion));
    }

    /**
     * Valida que un parametro obligatorio exista y no este vacio.
     * Si no existe, devuelve un error 400 y marca la respuesta para detener la ejecucion.
     */
    private void validarParametroObligatorio(HttpServletResponse response, String parametro, String nombre)
            throws IOException {

        if (parametro == null || parametro.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(
                JsonUtils.errorJson("El parametro " + nombre + " es obligatorio"));
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

    /**
     * Maneja las peticiones OPTIONS para CORS preflight.
     */
    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        configurarCors(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
