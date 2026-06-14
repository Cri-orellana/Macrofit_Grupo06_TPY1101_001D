package com.proyecto.macrofit.rutinaejercicio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaEntity;

@Repository
public interface RutinaRepository extends JpaRepository<RutinaEntity, Integer> {

    List<RutinaEntity> findByActivoCatalogoTrue();

    List<RutinaEntity> findByNombreRutinaContainingIgnoreCase(String nombreRutina);

    List<RutinaEntity> findByIdUsuarioCreadorAndEsBaseFalse(Integer idUsuarioCreador);
}
