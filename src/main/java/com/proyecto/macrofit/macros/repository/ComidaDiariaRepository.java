package com.proyecto.macrofit.macros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.macrofit.macros.model.entity.ComidaUsuarioEntity;

@Repository
public interface ComidaDiariaRepository extends JpaRepository<ComidaUsuarioEntity, Long> {
    List<ComidaUsuarioEntity> findByUserId(Long userId);

    public void deleteByUserId(Long userId);
}
