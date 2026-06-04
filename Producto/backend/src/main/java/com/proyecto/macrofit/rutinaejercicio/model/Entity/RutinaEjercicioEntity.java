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
@Table(name = "Rutina_ejercicio")
public class RutinaEjercicioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rutina_ejercicio")
    private Integer idRutinaEjercicio;

    @Column(name = "id_ejercicio")
    private Integer idEjercicio;

    @Column(name = "id_rutina")
    private Integer idRutina;

    @Column(name = "dia")
    private Integer dia;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "series")
    private Integer series;

    @Column(name = "tiempo_seg")
    private Integer tiempoSeg;

    @Column(name = "repeticiones")
    private Integer repeticiones;

    @Column(name = "peso_referencia")
    private Float pesoReferencia;

}