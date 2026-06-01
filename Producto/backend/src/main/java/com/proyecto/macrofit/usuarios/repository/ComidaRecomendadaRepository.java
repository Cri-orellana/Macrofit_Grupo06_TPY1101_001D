package com.proyecto.macrofit.usuarios.repository;

import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ComidaRecomendadaRepository extends JpaRepository<ComidaRecomendada, Integer> {

    @Query("SELECT c FROM ComidaRecomendada c WHERE c.tipo_alimentacion.id_tipo_alimentacion = :idTipo")
    List<ComidaRecomendada> findByTipoAlimentacionId(@Param("idTipo") Integer idTipo);
}