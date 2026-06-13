package com.proyecto.macrofit.macros.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.macrofit.macros.model.entity.ComidaNuevaEntity;
import com.proyecto.macrofit.macros.repository.ComidaNuevaRepository;

@Service
public class ComidaNuevaService {

    @Autowired
    private ComidaNuevaRepository comidaNRepository;

    public ComidaNuevaEntity agregarAlimento(ComidaNuevaEntity comida) {
        if (comida == null || comida.getCode() == null || comida.getCode().isBlank()) {
            throw new IllegalArgumentException("El código de barras es obligatorio");
        }
        return comidaNRepository.save(comida);
    }

    public Optional<ComidaNuevaEntity> obtenerPorCodigo(String code) {
        return comidaNRepository.findById(code);
    }

    public List<ComidaNuevaEntity> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarTodas();
        }
        return comidaNRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public boolean eliminarPorCodigo(String code) {
        if (comidaNRepository.existsById(code)) {
            comidaNRepository.deleteById(code);
            return true;
        }
        return false;
    }

    public List<ComidaNuevaEntity> listarTodas() {
        return comidaNRepository.findAll();
    }
}
