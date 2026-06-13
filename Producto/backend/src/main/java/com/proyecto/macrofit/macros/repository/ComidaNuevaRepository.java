package com.proyecto.macrofit.macros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.macrofit.macros.model.entity.ComidaNuevaEntity;

@Repository
public interface ComidaNuevaRepository extends JpaRepository<ComidaNuevaEntity, String> {

    List<ComidaNuevaEntity> findByNombreContainingIgnoreCase(String nombre);
}
