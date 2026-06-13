package com.proyecto.macrofit.macros.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "new_comida")
@Data
public class ComidaNuevaEntity {

    @Id
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    private String nombre;
    private Double calorias;
    private Double proteinas;
    private Double carbohidratos;
    private Double grasas;
}
