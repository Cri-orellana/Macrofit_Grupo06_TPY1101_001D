package com.proyecto.macrofit.usuarios.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class LibreTranslateService {

    @Value("${libretranslate.url:http://165.1.124.204:5000}")
    private String libreTranslateUrl;

    @Value("${libretranslate.api-key:}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public LibreTranslateService() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        this.restTemplate = new RestTemplate(factory);
    }

    public String traducirEnEsp(String textoIngles) {
        if (textoIngles == null || textoIngles.isBlank())
            return "";

        String textoLimpio = textoIngles.replaceAll("<[^>]*>", "").trim();
        if (textoLimpio.isEmpty())
            return textoLimpio;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = (apiKey == null || apiKey.isBlank())
                    ? Map.of("q", textoLimpio, "source", "en", "target", "es")
                    : Map.of("q", textoLimpio, "source", "en", "target", "es", "api_key", apiKey);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    libreTranslateUrl + "/translate", request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Object traducido = response.getBody().get("translatedText");
                if (traducido != null && !traducido.toString().isBlank()) {
                    return traducido.toString();
                }
            }
        } catch (Exception e) {

            System.err.println("⚠️ LibreTranslate no disponible ["
                    + textoLimpio.substring(0, Math.min(40, textoLimpio.length()))
                    + "]: " + e.getMessage());
        }

        return textoLimpio;
    }
}