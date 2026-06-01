package com.proyecto.macrofit.macros.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.proyecto.macrofit.macros.model.ComidaDto;

import tools.jackson.databind.ObjectMapper;

@Service
public class ComidaService {

    private final RestTemplate restTemplate;
    private final String BASE_URL = "https://world.openfoodfacts.org";
    private final ObjectMapper mapper = new ObjectMapper();

    public ComidaService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ComidaDto getComidaByBarra(String barra) {
        String url = String.format("%s/api/v0/product/%s.json", BASE_URL, barra);

        try {

            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "MiAppNutricion/1.0 (Android; tu-correo@dominio.com)");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> resp = response.getBody();

            if (resp == null)
                return null;

            Object comidaData = resp.get("product");
            if (comidaData == null)
                return null;

            return mapper.convertValue(comidaData, ComidaDto.class);
        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public List<ComidaDto> getComidasByNombre(String nombre) {
        String url = String.format("%s/cgi/search.pl?search_terms=%s&search_simple=1&action=process&json=1", BASE_URL,
                nombre);
        try {
            // También le ponemos las cabeceras a la búsqueda por nombre
            HttpHeaders headers = new HttpHeaders();
            headers.set("User-Agent", "MiAppNutricion/1.0 (Android; tu-correo@dominio.com)");
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, entity, Map.class);
            Map<String, Object> resp = response.getBody();

            if (resp == null)
                return Collections.emptyList();

            List<Map<String, Object>> comidaList = (List<Map<String, Object>>) resp.get("products");

            if (comidaList == null || comidaList.isEmpty()) {
                return Collections.emptyList();
            }

            // Mantiene tu mapeo con streams para devolver la lista de DTOs
            return comidaList.stream()
                    .map(comidaData -> mapper.convertValue(comidaData, ComidaDto.class))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

}
