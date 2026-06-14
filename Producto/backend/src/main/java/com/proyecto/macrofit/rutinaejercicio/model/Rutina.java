package com.proyecto.macrofit.rutinaejercicio.model;

import lombok.Data;

@Data
public class Rutina {

    private Integer idRutina;
    private Integer idUsuarioCreador;
    private String nombreRutina;
    private String descripcion;
    private Boolean esBase;
    private Boolean activoCatalogo;

}
