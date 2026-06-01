package com.proyecto.macrofit.rutinaejercicio.model.Entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Rutina_usuario")
public class RutinaUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id_rutina_usuario")
    private Integer idRutinaUsuario;

    @Column(name="id_rutina")
    private Integer idRutina;

    @Column(name="id_usuario")
    private Integer idUsuario;

    @Column(name="fecha_inicio")
    private LocalDate fechaInicio;

    @Column(name="fecha_fin")
    private LocalDate fechaFin;

    @Column(name="activo")
    private Boolean activo;

    
}
