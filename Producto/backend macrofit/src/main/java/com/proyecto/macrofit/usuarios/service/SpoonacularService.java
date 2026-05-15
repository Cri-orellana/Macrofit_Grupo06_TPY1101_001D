package com.proyecto.macrofit.usuarios.service;

import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpoonacularService {

    @Value("${spoonacular.api.key}")
    private String claveApi;

    @Value("${spoonacular.api.url}")
    private String urlBase;

    private final RestTemplate restTemplate;

    public SpoonacularService() {
        this.restTemplate = new RestTemplate();
    }

    // TRADUCTOR DE DIETAS
    private String traducirFiltroDieta(String dietaEspanol) {
        if (dietaEspanol == null || dietaEspanol.trim().isEmpty()) {
            return "";
        }
        String dietaLimpia = dietaEspanol.toLowerCase();
        if (dietaLimpia.contains("vegetariana"))
            return "vegetarian";
        if (dietaLimpia.contains("vegana"))
            return "vegan";
        if (dietaLimpia.contains("keto") || dietaLimpia.contains("cetog"))
            return "ketogenic";
        if (dietaLimpia.contains("paleo"))
            return "paleo";
        return "";
    }

    // TRADUCTOR DE TEXTOS Y LIMPIEZA
    private String traducirTituloAlEspanol(String textoIngles) {
        if (textoIngles == null || textoIngles.trim().isEmpty())
            return "";
        try {
            // Limpiar etiquetas HTML raras que mande la API
            String textoLimpio = textoIngles.replaceAll("<[^>]*>", "");

            // Usamos las variables de RestTemplate para evitar errores con espacios y el
            // símbolo "|"
            String urlTraductor = "https://api.mymemory.translated.net/get?q={texto}&langpair=en|es";

            Map<String, Object> respuestaTraduccion = restTemplate.getForObject(urlTraductor, Map.class, textoLimpio);

            if (respuestaTraduccion != null && respuestaTraduccion.containsKey("responseData")) {
                Map<String, Object> datosRespuesta = (Map<String, Object>) respuestaTraduccion.get("responseData");
                String textoTraducido = (String) datosRespuesta.get("translatedText");

                // Si la traducción no devuelve errores extraños de MyMemory, la usamos
                if (textoTraducido != null && !textoTraducido.contains("INVALID LANGUAGE")
                        && !textoTraducido.contains("MYMEMORY")) {
                    return textoTraducido;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error traduciendo texto: " + e.getMessage());
        }
        // Si todo falla, devolvemos el texto original limpio
        return textoIngles.replaceAll("<[^>]*>", "");
    }

    public List<ComidaRecomendada> buscarRecetasPersonalizadas(
            String tipoDieta,
            String ingredientes,
            Float maxCarbohidratos,
            Float minProteina,
            Float maxGrasa) {
        List<ComidaRecomendada> listaRecomendaciones = new ArrayList<>();

        try {
            String dietaTraducida = traducirFiltroDieta(tipoDieta);

            String urlFinal = UriComponentsBuilder.fromUriString(urlBase)
                    .queryParam("apiKey", claveApi)
                    .queryParam("diet", dietaTraducida)
                    .queryParam("includeIngredients", ingredientes)
                    .queryParam("maxCarbs", maxCarbohidratos)
                    .queryParam("minProtein", minProteina)
                    .queryParam("maxFat", maxGrasa)
                    .queryParam("number", 5)
                    .queryParam("addRecipeNutrition", true)
                    .queryParam("fillIngredients", true)
                    .queryParam("addRecipeInformation", true)
                    .queryParam("instructionsRequired", true)
                    .toUriString();

            Map<String, Object> respuestaSpoonacular = restTemplate.getForObject(urlFinal, Map.class);

            if (respuestaSpoonacular != null && respuestaSpoonacular.containsKey("results")) {
                List<Map<String, Object>> resultados = (List<Map<String, Object>>) respuestaSpoonacular.get("results");

                for (Map<String, Object> recetaIngles : resultados) {
                    ComidaRecomendada nuevaComida = new ComidaRecomendada();

                    nuevaComida.setId_comida((Integer) recetaIngles.get("id"));
                    nuevaComida.setFoto_comida((String) recetaIngles.get("image"));
                    nuevaComida.setDescripcion_comida("Recomendación personalizada.");

                    String tituloIngles = (String) recetaIngles.get("title");
                    nuevaComida.setNombre_comida(traducirTituloAlEspanol(tituloIngles));

                    List<String> listaIng = new ArrayList<>();
                    if (recetaIngles.containsKey("extendedIngredients")) {
                        List<Map<String, Object>> extIng = (List<Map<String, Object>>) recetaIngles
                                .get("extendedIngredients");
                        if (extIng != null) {
                            for (Map<String, Object> ing : extIng) {
                                String ingredienteOriginal = (String) ing.get("original");
                                listaIng.add(traducirTituloAlEspanol(ingredienteOriginal));
                            }
                        }
                    }
                    nuevaComida.setIngredientes_lista(listaIng);

                    List<String> listaPasos = new ArrayList<>();

                    if (recetaIngles.containsKey("analyzedInstructions")) {
                        List<Map<String, Object>> instructions = (List<Map<String, Object>>) recetaIngles
                                .get("analyzedInstructions");
                        if (instructions != null && !instructions.isEmpty()) {
                            List<Map<String, Object>> steps = (List<Map<String, Object>>) instructions.get(0)
                                    .get("steps");
                            if (steps != null) {
                                for (Map<String, Object> step : steps) {
                                    String pasoOriginal = (String) step.get("step");
                                    if (pasoOriginal != null && !pasoOriginal.trim().isEmpty()) {
                                        listaPasos.add(traducirTituloAlEspanol(pasoOriginal));
                                    }
                                }
                            }
                        }
                    }

                    if (listaPasos.isEmpty() && recetaIngles.containsKey("instructions")) {
                        String instruccionesGenerales = (String) recetaIngles.get("instructions");
                        if (instruccionesGenerales != null && !instruccionesGenerales.trim().isEmpty()) {
                            listaPasos.add(traducirTituloAlEspanol(instruccionesGenerales));
                        }
                    }

                    if (listaPasos.isEmpty()) {
                        listaPasos.add("Mezclar los ingredientes y preparar según preferencia.");
                    }

                    nuevaComida.setPreparacion_lista(listaPasos);

                    // 4. Macros y Peso
                    Map<String, Object> nutricion = (Map<String, Object>) recetaIngles.get("nutrition");
                    if (nutricion != null) {
                        if (nutricion.containsKey("weightPerServing")) {
                            Map<String, Object> pesoPorcion = (Map<String, Object>) nutricion.get("weightPerServing");
                            if (pesoPorcion != null) {
                                Number cantidadPeso = (Number) pesoPorcion.get("amount");
                                String unidadMedida = (String) pesoPorcion.get("unit");
                                if (cantidadPeso != null && unidadMedida != null) {
                                    nuevaComida.setCantidad_porcion(cantidadPeso.intValue() + unidadMedida);
                                }
                            }
                        }

                        if (nutricion.containsKey("nutrients")) {
                            List<Map<String, Object>> nutrientes = (List<Map<String, Object>>) nutricion
                                    .get("nutrients");
                            if (nutrientes != null) {
                                for (Map<String, Object> nutriente : nutrientes) {
                                    String nombreNutriente = (String) nutriente.get("name");
                                    Float cantidad = ((Number) nutriente.get("amount")).floatValue();

                                    switch (nombreNutriente) {
                                        case "Calories":
                                            nuevaComida.setCalorias_porcion(cantidad);
                                            break;
                                        case "Protein":
                                            nuevaComida.setProteina_porcion(cantidad);
                                            break;
                                        case "Carbohydrates":
                                            nuevaComida.setCarbohidratos_porcion(cantidad);
                                            break;
                                        case "Fat":
                                            nuevaComida.setGrasa_porcion(cantidad);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                    listaRecomendaciones.add(nuevaComida);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error crítico al consultar Spoonacular: " + e.getMessage());
        }

        return listaRecomendaciones;
    }
}