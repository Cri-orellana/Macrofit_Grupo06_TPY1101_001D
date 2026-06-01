package com.proyecto.macrofit.rutinaejercicio.model.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Ejercicio")
public class EjercicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_ejercicio")
    private Integer idEjercicio;

    @Column(name="nombre_ejercicio")
    private String nombreEjercicio;

    @Column(name="descripcion", length = 500)
    private String descripcion;

    @Column(name="imagen_ejercicio")
    private String imagenEjercicio;

    @Column(name="video_ejercicio")
    private String videoEjercicio;

    @Column(name="activo_catalogo")
    private Boolean activoCatalogo;

    @Column(name="zona_muscular")
    private String zonaMuscular;

    @Column(name="implemento")
    private String implemento;

    @Column(name="nivel_dificultad")
    private String nivelDificultad;

    @Column(name="musculo_objetivo")
    private String musculoObjetivo;
    
}
