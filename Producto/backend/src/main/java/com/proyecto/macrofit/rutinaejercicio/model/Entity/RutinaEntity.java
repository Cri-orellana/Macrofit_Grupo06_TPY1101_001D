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
@Table(name = "Rutina")
public class RutinaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina")
    private Integer idRutina;

    @Column(name = "id_usuario_creador")
    private Integer idUsuarioCreador;

    @Column(name = "nombre_rutina")
    private String nombreRutina;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "es_base")
    private Boolean esBase;

    @Column(name = "activo_catalogo")
    private Boolean activoCatalogo;

}