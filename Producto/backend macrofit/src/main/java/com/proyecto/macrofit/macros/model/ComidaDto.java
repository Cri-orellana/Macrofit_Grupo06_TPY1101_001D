package com.proyecto.macrofit.macros.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComidaDto {
    @JsonProperty("code")
    private String barra;
    @JsonProperty("product_name")
    private String nombre;
    @JsonProperty("nutriments")
    private Nutriments nutriments;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Nutriments {
        @JsonProperty("energy-kcal_100g")
        private Double calorias;

        @JsonProperty("carbohydrates_100g")
        private Double carbohidratos;

        @JsonProperty("proteins_100g")
        private Double proteinas;

        @JsonProperty("fat_100g")
        private Double grasas;
    }
}