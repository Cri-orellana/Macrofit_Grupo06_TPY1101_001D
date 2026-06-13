package com.proyecto.macrofit.usuarios.repository;

import com.proyecto.macrofit.usuarios.model.RecetaCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaCacheRepository extends JpaRepository<RecetaCache, Integer> {

    // Busca todas las recetas guardadas para una combinación de parámetros
    List<RecetaCache> findByCacheKey(String cacheKey);

    // Para verificar si ya existe
    boolean existsByCacheKey(String cacheKey);
}