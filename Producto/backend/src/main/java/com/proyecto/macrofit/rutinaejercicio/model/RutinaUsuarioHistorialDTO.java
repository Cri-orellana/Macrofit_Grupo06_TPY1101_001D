package com.proyecto.macrofit.rutinaejercicio.model;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RutinaUsuarioHistorialDTO {
    private Integer idRutinaUsuario;
    private Integer idRutina;
    private String nombreRutina;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean activo;
}
