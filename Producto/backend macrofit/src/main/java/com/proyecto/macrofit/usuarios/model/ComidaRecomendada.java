package com.proyecto.macrofit.usuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Comida_Recomendada")
public class ComidaRecomendada {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_comida")
    private Integer id_comida;

    @Column(name = "nombre_comida")
    private String nombre_comida;

    @Column(name = "descripcion_comida")
    private String descripcion_comida;

    @Column(name = "calorias_porcion")
    private Float calorias_porcion;

    @Column(name = "proteina_porcion")
    private Float proteina_porcion;

    @Column(name = "carbohidratos_porcion")
    private Float carbohidratos_porcion;

    @Column(name = "grasa_porcion")
    private Float grasa_porcion;

    // Relación con la tabla Tipo_Alimentacion
    @ManyToOne
    @JoinColumn(name = "id_tipo_alimentacion")
    private TipoAlimentacion tipo_alimentacion;

    // Constructor vacío
    public ComidaRecomendada() {
    }

    public ComidaRecomendada(String nombre_comida, String descripcion_comida, Float calorias_porcion,
            Float proteina_porcion, Float carbohidratos_porcion, Float grasa_porcion,
            TipoAlimentacion tipo_alimentacion) {
        this.nombre_comida = nombre_comida;
        this.descripcion_comida = descripcion_comida;
        this.calorias_porcion = calorias_porcion;
        this.proteina_porcion = proteina_porcion;
        this.carbohidratos_porcion = carbohidratos_porcion;
        this.grasa_porcion = grasa_porcion;
        this.tipo_alimentacion = tipo_alimentacion;
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
}