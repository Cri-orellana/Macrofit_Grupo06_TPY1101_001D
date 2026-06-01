package com.proyecto.macrofit.macros.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "comida")
@Data
public class ComidaUsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String code;
    private String nombre;
    private Double porcion;
    // Macros
    private Double calorias;
    private Double carbohidratos;
    private Double proteinas;
    private Double grasas;

    private LocalDateTime fechareg;
}
