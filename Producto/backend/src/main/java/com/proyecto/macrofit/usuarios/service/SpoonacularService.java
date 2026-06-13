package com.proyecto.macrofit.usuarios.service;

import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import com.proyecto.macrofit.usuarios.model.RecetaCache;
import com.proyecto.macrofit.usuarios.repository.RecetaCacheRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SpoonacularService {

    @Autowired
    private RecetaCacheRepository cacheRepo;

    @Value("${spoonacular.api.key}")
    private String claveApi;

    @Value("${spoonacular.api.url}")
    private String urlBase;

    private final RestTemplate restTemplate;

    public SpoonacularService() {
        this.restTemplate = new RestTemplate();
    }

    private String generarCacheKey(String tipoDieta, String ingredientes,
            Float maxCarbs, Float minProt, Float maxGrasa) {
        return String.format("%s|%s|%.1f|%.1f|%.1f",
                tipoDieta.toLowerCase().trim(),
                ingredientes.toLowerCase().trim(),
                maxCarbs, minProt, maxGrasa);
    }

    // --- Convierte RecetaCache → ComidaRecomendada para devolver a la app ---
    private ComidaRecomendada cacheAComida(RecetaCache cache) {
        ComidaRecomendada c = new ComidaRecomendada();
        c.setId_comida(cache.getSpoonacularId());
        c.setNombre_comida(cache.getNombre_comida());
        c.setDescripcion_comida(cache.getDescripcion_comida());
        c.setCantidad_porcion(cache.getCantidad_porcion());
        c.setCalorias_porcion(cache.getCalorias_porcion());
        c.setProteina_porcion(cache.getProteina_porcion());
        c.setCarbohidratos_porcion(cache.getCarbohidratos_porcion());
        c.setGrasa_porcion(cache.getGrasa_porcion());
        c.setFoto_comida(cache.getFoto_comida());
        c.setIngredientes_lista(cache.getIngredientes());
        c.setPreparacion_lista(cache.getPreparacion());
        return c;
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

            String url = "https://api.mymemory.translated.net/get?q=" + textoLimpio
                    + "&langpair=en|es&de=cristianj.orellanaa@gmail.com";
            Map<String, Object> respuestaTraduccion = restTemplate.getForObject(url, Map.class);

            if (respuestaTraduccion != null && respuestaTraduccion.containsKey("responseData")) {
                Map<String, Object> datosRespuesta = (Map<String, Object>) respuestaTraduccion.get("responseData");
                String textoTraducido = (String) datosRespuesta.get("translatedText");

                if (textoTraducido != null && !textoTraducido.contains("INVALID LANGUAGE")
                        && !textoTraducido.contains("MYMEMORY")) {
                    return textoTraducido;
                }
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error traduciendo texto: " + e.getMessage());
        }
        return textoIngles.replaceAll("<[^>]*>", "");
    }

    public int retraducirRecetasEnIngles() {
        List<RecetaCache> todas = cacheRepo.findAll();
        int traducidas = 0;

        for (RecetaCache receta : todas) {

            boolean necesitaTraduccion = false;

            // Detectar si el nombre parece inglés (contiene palabras comunes en inglés)
            String nombre = receta.getNombre_comida();
            if (nombre != null && esIngles(nombre)) {
                receta.setNombre_comida(traducirTituloAlEspanol(nombre));
                necesitaTraduccion = true;
            }

            // Re-traducir ingredientes
            List<String> ings = receta.getIngredientes();
            if (ings != null && !ings.isEmpty() && esIngles(ings.get(0))) {
                List<String> ingsTraducidos = new ArrayList<>();
                for (String ing : ings) {
                    ingsTraducidos.add(traducirTituloAlEspanol(ing));
                }
                receta.setIngredientes(ingsTraducidos);
                necesitaTraduccion = true;
            }

            // Re-traducir pasos de preparación
            List<String> pasos = receta.getPreparacion();
            if (pasos != null && !pasos.isEmpty() && esIngles(pasos.get(0))) {
                List<String> pasosTraducidos = new ArrayList<>();
                for (String paso : pasos) {
                    pasosTraducidos.add(traducirTituloAlEspanol(paso));
                }
                receta.setPreparacion(pasosTraducidos);
                necesitaTraduccion = true;
            }

            if (necesitaTraduccion) {
                cacheRepo.save(receta);
                traducidas++;
                System.out.println("✅ Traducida: " + nombre);

                // Pausa de 1 segundo entre recetas para no saturar MyMemory
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) {
                }
            }
        }

        return traducidas;
    }

    // Detector simple de inglés: busca palabras muy comunes en recetas en inglés
    private boolean esIngles(String texto) {
        if (texto == null || texto.isBlank())
            return false;
        String lower = texto.toLowerCase();
        return lower.contains(" the ") || lower.contains(" with ") ||
                lower.contains(" and ") || lower.contains(" of ") ||
                lower.contains(" in ") || lower.contains(" to ") ||
                lower.contains(" cup") || lower.contains(" tbsp") ||
                lower.contains(" tsp") || lower.contains("chicken") ||
                lower.contains("rice") || lower.contains("sauce");
    }

    public List<ComidaRecomendada> buscarRecetasPersonalizadas(
            String tipoDieta,
            String ingredientes,
            Float maxCarbohidratos,
            Float minProteina,
            Float maxGrasa) {

        String cacheKey = generarCacheKey(tipoDieta, ingredientes,
                maxCarbohidratos, minProteina, maxGrasa);

        // Si ya hay resultados para esta búsqueda, devolver directo
        List<RecetaCache> enCache = cacheRepo.findByCacheKey(cacheKey);
        if (!enCache.isEmpty()) {
            System.out.println("✅ Cache HIT: " + cacheKey);
            return enCache.stream().map(this::cacheAComida).toList();
        }

        System.out.println("🌐 Cache MISS: " + cacheKey);

        // Llamar a Spoonacular + traducir (tu código actual sin cambios)
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

            // Guardar las recetas, verificando que no existan previamente
            for (ComidaRecomendada comida : listaRecomendaciones) {

                // ✅ Si esta receta ya existe en cache, no la vuelvas a guardar
                if (cacheRepo.existsBySpoonacularId(comida.getId_comida())) {
                    System.out.println("⏭️ Ya existe en cache: " + comida.getNombre_comida());
                    continue;
                }

                RecetaCache cache = new RecetaCache();
                cache.setCacheKey(cacheKey);
                cache.setSpoonacularId(comida.getId_comida());
                cache.setNombre_comida(comida.getNombre_comida());
                cache.setDescripcion_comida(comida.getDescripcion_comida());
                cache.setCantidad_porcion(comida.getCantidad_porcion());
                cache.setCalorias_porcion(comida.getCalorias_porcion());
                cache.setProteina_porcion(comida.getProteina_porcion());
                cache.setCarbohidratos_porcion(comida.getCarbohidratos_porcion());
                cache.setGrasa_porcion(comida.getGrasa_porcion());
                cache.setFoto_comida(comida.getFoto_comida());
                cache.setIngredientes(comida.getIngredientes_lista());
                cache.setPreparacion(comida.getPreparacion_lista());
                cacheRepo.save(cache);
                System.out.println("💾 Guardada: " + comida.getNombre_comida());
            }

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }

        return listaRecomendaciones;
    }
}