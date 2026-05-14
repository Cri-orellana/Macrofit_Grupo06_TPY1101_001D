package com.proyecto.macrofit.usuarios.repository; // Ajusta tu paquete

import org.springframework.data.jpa.repository.JpaRepository;

import com.proyecto.macrofit.usuarios.model.NvActividad;

public interface NvActividadRepository extends JpaRepository<NvActividad, Integer> {
}