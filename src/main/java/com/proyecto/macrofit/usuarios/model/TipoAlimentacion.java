package com.proyecto.macrofit.usuarios.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Tipo_Alimentacion")
public class TipoAlimentacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo_alimentacion")
    private Integer id_tipo_alimentacion;

    @Column(name = "nombre_tipo")
    private String nombre_tipo;

    // Constructor vacío
    public TipoAlimentacion() {
    }

    public TipoAlimentacion(Integer id_tipo_alimentacion, String nombre_tipo) {
        this.id_tipo_alimentacion = id_tipo_alimentacion;
        this.nombre_tipo = nombre_tipo;
    }

    public Integer getId_tipo_alimentacion() {
        return id_tipo_alimentacion;
    }

    public void setId_tipo_alimentacion(Integer id_tipo_alimentacion) {
        this.id_tipo_alimentacion = id_tipo_alimentacion;
    }

    public String getNombre_tipo() {
        return nombre_tipo;
    }

    public void setNombre_tipo(String nombre_tipo) {
        this.nombre_tipo = nombre_tipo;
    }
}