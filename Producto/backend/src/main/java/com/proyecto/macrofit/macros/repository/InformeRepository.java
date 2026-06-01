package com.proyecto.macrofit.macros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.macrofit.macros.model.Informe;

@Repository
public interface InformeRepository extends JpaRepository<Informe, String> {
}