package com.proyecto.macrofit.usuarios.repository;

import com.proyecto.macrofit.usuarios.model.TipoAlimentacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TipoAlimentacionRepository extends JpaRepository<TipoAlimentacion, Integer> {
}