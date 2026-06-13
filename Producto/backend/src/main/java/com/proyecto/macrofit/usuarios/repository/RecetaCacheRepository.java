package com.proyecto.macrofit.usuarios.repository;

import com.proyecto.macrofit.usuarios.model.RecetaCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RecetaCacheRepository extends JpaRepository<RecetaCache, Integer> {
    List<RecetaCache> findByCacheKey(String cacheKey);

    boolean existsByCacheKey(String cacheKey);

    boolean existsBySpoonacularId(Integer spoonacularId);
}