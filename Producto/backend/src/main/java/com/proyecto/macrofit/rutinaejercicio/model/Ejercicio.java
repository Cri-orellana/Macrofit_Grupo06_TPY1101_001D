package com.proyecto.macrofit.rutinaejercicio.model;

import lombok.Data;

@Data
public class Ejercicio {

    private Integer idEjercicio;
    private String nombreEjercicio;
    private String descripcion;
    private String imagenEjercicio;
    private String videoEjercicio;
    private Boolean activoCatalogo;
    private String zonaMuscular;
    private String implemento;
    private String nivelDificultad;
    private String musculoObjetivo;
    
}
