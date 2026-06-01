package com.proyecto.macrofit.macros.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.proyecto.macrofit.macros.model.entity.ComidaHistorialEntity;
import com.proyecto.macrofit.macros.model.entity.ComidaUsuarioEntity;
import com.proyecto.macrofit.macros.repository.ComidaDiariaRepository;
import com.proyecto.macrofit.macros.repository.ComidaHistorialRepository;

import jakarta.transaction.Transactional;

@Service
public class CierreService {
    private final ComidaDiariaRepository diarioRepository;
    private final ComidaHistorialRepository historialRepository;

    public CierreService(ComidaDiariaRepository diarioRepository, ComidaHistorialRepository historialRepository) {
        this.diarioRepository = diarioRepository;
        this.historialRepository = historialRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar a medianoche todos los días
    @Transactional
    public void cerrarDia() {
        // Mover las comidas del día al historial
        List<ComidaUsuarioEntity> comidasDiarias = diarioRepository.findAll();
        if (!comidasDiarias.isEmpty()) {
            List<ComidaHistorialEntity> historialGuardado = comidasDiarias.stream().map(diario -> {
                ComidaHistorialEntity historial = new ComidaHistorialEntity();
                historial.setUserId(diario.getUserId());
                historial.setCode(diario.getCode());
                historial.setNombre(diario.getNombre());
                historial.setPorcion(diario.getPorcion());
                historial.setCalorias(diario.getCalorias());
                historial.setProteinas(diario.getProteinas());
                historial.setCarbohidratos(diario.getCarbohidratos());
                historial.setGrasas(diario.getGrasas());
                historial.setFechareg(diario.getFechareg());

                return historial;
            }).collect(Collectors.toList());
            historialRepository.saveAll(historialGuardado);

        }
        diarioRepository.deleteAll();
    }

    public List<ComidaHistorialEntity> obtenerHistorial(Long userId) {
        return historialRepository.findByUserId(userId);
    }
}
