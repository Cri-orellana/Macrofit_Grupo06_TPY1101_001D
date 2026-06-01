package com.proyecto.macrofit.rutinaejercicio.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaUsuarioEntity;

@Repository
public interface RutinaUsuarioRepository extends JpaRepository<RutinaUsuarioEntity,Integer>{

    List<RutinaUsuarioEntity> findByIdUsuario(Integer idUsuario);

    List<RutinaUsuarioEntity> findByIdUsuarioAndActivoTrue(Integer idUsuario);

    List<RutinaUsuarioEntity> findByIdUsuarioAndActivoFalse(Integer idUsuario);

    List<RutinaUsuarioEntity> findByIdRutina(Integer idRutina);
}
