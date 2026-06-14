package com.proyecto.macrofit.rutinaejercicio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaEjercicioEntity;

@Repository
public interface RutinaEjercicioRepository extends JpaRepository<RutinaEjercicioEntity, Integer> {

    List<RutinaEjercicioEntity> findByIdRutinaOrderByOrdenAsc(Integer idRutina);

    List<RutinaEjercicioEntity> findByIdEjercicio(Integer idEjercicio);

    void deleteByIdRutina(Integer idRutina);

    List<RutinaEjercicioEntity> findByIdRutina(Integer idRutina);

}
