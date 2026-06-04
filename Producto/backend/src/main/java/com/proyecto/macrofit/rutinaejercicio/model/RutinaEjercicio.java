package com.proyecto.macrofit.rutinaejercicio.model;

import lombok.Data;

@Data
public class RutinaEjercicio {

    private Integer idRutinaEjercicio;
    private Integer idEjercicio;
    private Integer idRutina;
    private Integer dia;
    private Integer orden;
    private Integer series;
    private Integer tiempoSeg;
    private Integer repeticiones;
    private Float pesoReferencia;

}