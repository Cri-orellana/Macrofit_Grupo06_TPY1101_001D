package com.proyecto.macrofit.macros.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.proyecto.macrofit.macros.model.ComidaDto;
import com.proyecto.macrofit.macros.model.entity.ComidaUsuarioEntity;
import com.proyecto.macrofit.macros.repository.ComidaDiariaRepository;

@Service
public class ComidasDiarias {
    private final ComidaDiariaRepository diarioRepository;

    public ComidasDiarias(ComidaDiariaRepository diarioRepository) {
        this.diarioRepository = diarioRepository;
    }

    public ComidaUsuarioEntity agregarComidaDiaria(ComidaDto comida, Long userId, Double porcion) {
        ComidaUsuarioEntity nuevaComida = new ComidaUsuarioEntity();
        nuevaComida.setUserId(userId);
        nuevaComida.setCode(comida.getBarra());
        nuevaComida.setNombre(comida.getNombre());
        nuevaComida.setPorcion(porcion);
        nuevaComida.setFechareg(LocalDateTime.now());

        if (comida.getNutriments() != null && comida.getNutriments().getCalorias() != null) {
            Double calorias100g = comida.getNutriments().getCalorias();
            Double proteina100g = comida.getNutriments().getProteinas();
            Double carbohidratos100g = comida.getNutriments().getCarbohidratos();
            Double grasas100g = comida.getNutriments().getGrasas();
            Double caloriasReal = (calorias100g * porcion) / 100;
            Double proteinaReal = (proteina100g * porcion) / 100;
            Double carbohidratosReal = (carbohidratos100g * porcion) / 100;
            Double grasasReal = (grasas100g * porcion) / 100;

            nuevaComida.setCalorias(caloriasReal);
            nuevaComida.setProteinas(proteinaReal);
            nuevaComida.setCarbohidratos(carbohidratosReal);
            nuevaComida.setGrasas(grasasReal);
        }
        return diarioRepository.save(nuevaComida);
    }

    public List<ComidaUsuarioEntity> obtenerComidasDiarias(Long userId) {
        return diarioRepository.findByUserId(userId);
    }

    public void eliminarComidaDiaria(Long id) {
        diarioRepository.deleteById(id);
    }

    public ComidaUsuarioEntity actualizarComidaDiaria(Long id, Double nuevaPorcion) {
        var comida = diarioRepository.findById(id).orElse(null);
        if (comida == null) {
            return null;
        }

        // Calcular factor de cambio proporcionalmente
        Double porcionAnterior = comida.getPorcion();
        if (porcionAnterior == null || porcionAnterior == 0) {
            return null;
        }

        Double factor = nuevaPorcion / porcionAnterior;

        // Recalcular macros proporcionalmente
        comida.setPorcion(nuevaPorcion);
        if (comida.getCalorias() != null) {
            comida.setCalorias(comida.getCalorias() * factor);
        }
        if (comida.getProteinas() != null) {
            comida.setProteinas(comida.getProteinas() * factor);
        }
        if (comida.getCarbohidratos() != null) {
            comida.setCarbohidratos(comida.getCarbohidratos() * factor);
        }
        if (comida.getGrasas() != null) {
            comida.setGrasas(comida.getGrasas() * factor);
        }

        return diarioRepository.save(comida);
    }

}