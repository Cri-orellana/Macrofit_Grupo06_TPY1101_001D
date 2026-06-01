package com.proyecto.macrofit.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.proyecto.macrofit.usuarios.model.Objetivo;

public interface ObjetivoRepository extends JpaRepository<Objetivo, Integer> {
}