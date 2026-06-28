package com.habittracker.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * Utilidad para manejar JSON en los Servlets.
 * Proporciona metodos para leer el cuerpo de una peticion y convertirlo a objetos,
 * y para escribir respuestas JSON.
 */
public class JsonUtils {

    private static final Gson gson = new Gson();

    /**
     * Lee el cuerpo de una peticion HTTP y lo convierte al tipo especificado.
     *
     * @param <T>   Tipo de objeto esperado
     * @param request La peticion HTTP entrante
     * @param tipo   Clase del tipo esperado
     * @return Objeto del tipo especificado con los datos del JSON
     * @throws IOException Si hay un error de lectura
     * @throws JsonSyntaxException Si el JSON no tiene el formato esperado
     */
    public static <T> T leerCuerpo(HttpServletRequest request, Class<T> tipo)
            throws IOException {
        StringBuilder cuerpo = new StringBuilder();
        String linea;

        try (BufferedReader lector = request.getReader()) {
            while ((linea = lector.readLine()) != null) {
                cuerpo.append(linea);
            }
        }

        return gson.fromJson(cuerpo.toString(), tipo);
    }

    /**
     * Convierte un objeto a su representacion JSON.
     *
     * @param objeto El objeto a convertir
     * @return String con el JSON del objeto
     */
    public static String aJson(Object objeto) {
        return gson.toJson(objeto);
    }

    /**
     * Crea un objeto JSON con un campo de error.
     *
     * @param mensaje Descripcion del error
     * @return String con el JSON: {"error": "mensaje"}
     */
    public static String errorJson(String mensaje) {
        JsonObject error = new JsonObject();
        error.addProperty("error", mensaje);
        return error.toString();
    }
}
