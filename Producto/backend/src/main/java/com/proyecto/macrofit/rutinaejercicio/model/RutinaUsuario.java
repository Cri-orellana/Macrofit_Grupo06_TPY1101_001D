package com.proyecto.macrofit.rutinaejercicio.model;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RutinaUsuario {

    private Integer idRutinaUsuario;
    private Integer idRutina;
    private Integer idUsuario;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;

    
}
