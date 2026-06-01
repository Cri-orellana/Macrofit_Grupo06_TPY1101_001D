package com.proyecto.macrofit.usuarios.model;

import jakarta.persistence.*;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import java.util.List;

@Entity
public class ComidaRecomendada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_comida;

    private String nombre_comida;
    private String descripcion_comida;
    private String cantidad_porcion = "100g";

    private Float calorias_porcion;
    private Float proteina_porcion;
    private Float carbohidratos_porcion;
    private Float grasa_porcion;

    @ManyToOne
    @JoinColumn(name = "id_tipo_alimentacion")
    private TipoAlimentacion tipo_alimentacion;

    // --- CAMPOS PARA EL POP-UP DE LA APP (NO SE GUARDAN EN BD) ---
    @Transient
    private String foto_comida;

    @Transient
    private List<String> ingredientes_lista;

    @Transient
    private List<String> preparacion_lista;

    public ComidaRecomendada() {
    }

    public ComidaRecomendada(String nombre_comida, String descripcion_comida,
            float calorias_porcion, float proteina_porcion,
            float carbohidratos_porcion, float grasa_porcion,
            TipoAlimentacion tipo_alimentacion) {
        this.nombre_comida = nombre_comida;
        this.descripcion_comida = descripcion_comida;
        this.calorias_porcion = calorias_porcion;
        this.proteina_porcion = proteina_porcion;
        this.carbohidratos_porcion = carbohidratos_porcion;
        this.grasa_porcion = grasa_porcion;
        this.tipo_alimentacion = tipo_alimentacion;
        this.cantidad_porcion = "100g";
    }

    public Integer getId_comida() {
        return id_comida;
    }

    public void setId_comida(Integer id_comida) {
        this.id_comida = id_comida;
    }

    public String getNombre_comida() {
        return nombre_comida;
    }

    public void setNombre_comida(String nombre_comida) {
        this.nombre_comida = nombre_comida;
    }

    public String getDescripcion_comida() {
        return descripcion_comida;
    }

    public void setDescripcion_comida(String descripcion_comida) {
        this.descripcion_comida = descripcion_comida;
    }

    public String getCantidad_porcion() {
        return cantidad_porcion;
    }

    public void setCantidad_porcion(String cantidad_porcion) {
        this.cantidad_porcion = cantidad_porcion;
    }

    public Float getCalorias_porcion() {
        return calorias_porcion;
    }

    public void setCalorias_porcion(Float calorias_porcion) {
        this.calorias_porcion = calorias_porcion;
    }

    public Float getProteina_porcion() {
        return proteina_porcion;
    }

    public void setProteina_porcion(Float proteina_porcion) {
        this.proteina_porcion = proteina_porcion;
    }

    public Float getCarbohidratos_porcion() {
        return carbohidratos_porcion;
    }

    public void setCarbohidratos_porcion(Float carbohidratos_porcion) {
        this.carbohidratos_porcion = carbohidratos_porcion;
    }

    public Float getGrasa_porcion() {
        return grasa_porcion;
    }

    public void setGrasa_porcion(Float grasa_porcion) {
        this.grasa_porcion = grasa_porcion;
    }

    public TipoAlimentacion getTipo_alimentacion() {
        return tipo_alimentacion;
    }

    public void setTipo_alimentacion(TipoAlimentacion tipo_alimentacion) {
        this.tipo_alimentacion = tipo_alimentacion;
    }

    public String getFoto_comida() {
        return foto_comida;
    }

    public void setFoto_comida(String foto_comida) {
        this.foto_comida = foto_comida;
    }

    public List<String> getIngredientes_lista() {
        return ingredientes_lista;
    }

    public void setIngredientes_lista(List<String> ingredientes_lista) {
        this.ingredientes_lista = ingredientes_lista;
    }

    public List<String> getPreparacion_lista() {
        return preparacion_lista;
    }

    public void setPreparacion_lista(List<String> preparacion_lista) {
        this.preparacion_lista = preparacion_lista;
    }
}