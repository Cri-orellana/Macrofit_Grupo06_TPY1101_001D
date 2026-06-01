package com.proyecto.macrofit.rutinaejercicio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.macrofit.rutinaejercicio.model.Entity.EjercicioEntity;

@Repository
public interface EjercicioRepository extends JpaRepository<EjercicioEntity, Integer>{

    List<EjercicioEntity> findByActivoCatalogoTrue();

    //cambiar idzona idimplemento por los atributos del model/ Listo?/ probar opciones de metodos repository...
    List<EjercicioEntity> findByZonaMuscularIgnoreCase(String zonaMuscular);

    List<EjercicioEntity> findByImplementoIgnoreCase(String implemento);

    List<EjercicioEntity> findByNivelDificultadIgnoreCase(String nivelDificultad);

    List<EjercicioEntity> findByMusculoObjetivoIgnoreCase(String musculoObjetivo);

    List<EjercicioEntity> findByNombreEjercicioContainingIgnoreCase(String nombreEjercicio);

    
}
