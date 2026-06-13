package com.proyecto.macrofit.usuarios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "receta_cache", indexes = {
        @Index(name = "idx_cache_key", columnList = "cache_key")
})
public class RecetaCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "cache_key", nullable = false, length = 300)
    private String cacheKey;

    @Column(name = "spoonacular_id")
    private Integer spoonacularId;

    private String nombre_comida;
    private String descripcion_comida;
    private String cantidad_porcion;
    private Float calorias_porcion;
    private Float proteina_porcion;
    private Float carbohidratos_porcion;
    private Float grasa_porcion;
    private String foto_comida;

    @Column(columnDefinition = "TEXT")
    private String ingredientes_json;

    @Column(columnDefinition = "TEXT")
    private String preparacion_json;

    @Column(name = "creado_en")
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Transient
    private static final ObjectMapper mapper = new ObjectMapper();

    public void setIngredientes(List<String> lista) {
        try {
            this.ingredientes_json = mapper.writeValueAsString(lista);
        } catch (Exception e) {
            this.ingredientes_json = "[]";
        }
    }

    public List<String> getIngredientes() {
        try {
            return mapper.readValue(ingredientes_json, new TypeReference<>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    public void setPreparacion(List<String> lista) {
        try {
            this.preparacion_json = mapper.writeValueAsString(lista);
        } catch (Exception e) {
            this.preparacion_json = "[]";
        }
    }

    public List<String> getPreparacion() {
        try {
            return mapper.readValue(preparacion_json, new TypeReference<>() {
            });
        } catch (Exception e) {
            return List.of();
        }
    }

    // Getters y setters estándar
    public Integer getId() {
        return id;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public Integer getSpoonacularId() {
        return spoonacularId;
    }

    public void setSpoonacularId(Integer spoonacularId) {
        this.spoonacularId = spoonacularId;
    }

    public String getNombre_comida() {
        return nombre_comida;
    }

    public void setNombre_comida(String n) {
        this.nombre_comida = n;
    }

    public String getDescripcion_comida() {
        return descripcion_comida;
    }

    public void setDescripcion_comida(String d) {
        this.descripcion_comida = d;
    }

    public String getCantidad_porcion() {
        return cantidad_porcion;
    }

    public void setCantidad_porcion(String c) {
        this.cantidad_porcion = c;
    }

    public Float getCalorias_porcion() {
        return calorias_porcion;
    }

    public void setCalorias_porcion(Float c) {
        this.calorias_porcion = c;
    }

    public Float getProteina_porcion() {
        return proteina_porcion;
    }

    public void setProteina_porcion(Float p) {
        this.proteina_porcion = p;
    }

    public Float getCarbohidratos_porcion() {
        return carbohidratos_porcion;
    }

    public void setCarbohidratos_porcion(Float c) {
        this.carbohidratos_porcion = c;
    }

    public Float getGrasa_porcion() {
        return grasa_porcion;
    }

    public void setGrasa_porcion(Float g) {
        this.grasa_porcion = g;
    }

    public String getFoto_comida() {
        return foto_comida;
    }

    public void setFoto_comida(String f) {
        this.foto_comida = f;
    }

    public String getIngredientes_json() {
        return ingredientes_json;
    }

    public String getPreparacion_json() {
        return preparacion_json;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime c) {
        this.creadoEn = c;
    }
}