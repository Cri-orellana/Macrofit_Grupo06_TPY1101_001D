package com.proyecto.macrofit.usuarios.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.beans.factory.annotation.Value;
import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SpoonacularService {
    @Value("${API_KEY}")
    private String claveapi;

    @Value("${API_URL}")
    private String urlBase;

    private final RestTemplate restTemplate;

    public SpoonacularService(){
        this.restTemplate = new RestTemplate();
    }

    public List<ComidaRecomendada> buscarRecetaPersonalizada(
        String tipoDieta,
        String ingredientes,
        Float maxCarbohidratos,
        Float minProteina,
        Float maxGrasa
    ){
        String urlFinal = UriComponentsBuilder.fromHttpUrl(urlBase)
            .queryParam("apiKey",claveapi)
            .queryParam("diet",tipoDieta)
            .queryParam("includeIngredients",ingredientes)
            .queryParam("maxCarbs",maxCarbohidratos)
            .queryParam("minProtein",minProteina)
            .queryParam("maxFat",maxGrasa)
            .queryParam("number",5)
            .queryParam("addRecipeNutrition",true)
            .toUriString();

            Map<String, Object>respuestaSpoonacular =restTemplate.getForObject(urlFinal, Map.class);

            List<ComidaRecomendada>listaRecomendaciones =new ArrayList<>();

            if (respuestaSpoonacular != null && respuestaSpoonacular.containsKey("results"))
            {
                List<Map<String, Object>>resultados = (List<Map<String, Object>>) respuestaSpoonacular.get("results");
            
                for(Map<>)
            }
    }


}
