package com.proyecto.macrofit.macros.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.Data;

@Entity
@Table(name = "comida_historial")
@Data
public class ComidaHistorialEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userId")
    private Long userId;

    private String code;
    private String nombre;
    private Double porcion;
    private Double calorias;
    private Double carbohidratos;
    private Double proteinas;
    private Double grasas;
    private LocalDateTime fechareg;
}
