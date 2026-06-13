package com.proyecto.macrofit.usuarios.repository;

import com.proyecto.macrofit.usuarios.model.RecetaCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaCacheRepository extends JpaRepository<RecetaCache, Integer> {

    // Trae las 5 recetas de una búsqueda
    List<RecetaCache> findByCacheKey(String cacheKey);

    // Verifica si ya existe esa búsqueda guardada
    boolean existsByCacheKey(String cacheKey);
}