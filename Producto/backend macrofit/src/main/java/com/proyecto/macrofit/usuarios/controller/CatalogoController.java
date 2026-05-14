package com.proyecto.macrofit.usuarios.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.macrofit.usuarios.model.NvActividad;
import com.proyecto.macrofit.usuarios.model.Objetivo;
import com.proyecto.macrofit.usuarios.repository.NvActividadRepository;
import com.proyecto.macrofit.usuarios.repository.ObjetivoRepository;

import java.util.List;

@RestController
@RequestMapping("/api/catalogos")
public class CatalogoController {

    @Autowired
    private ObjetivoRepository objetivoRepository;

    @Autowired
    private NvActividadRepository nvActividadRepository;

    @GetMapping("/objetivos")
    public List<Objetivo> obtenerObjetivos() {
        return objetivoRepository.findAll();
    }

    @GetMapping("/actividades")
    public List<NvActividad> obtenerActividades() {
        return nvActividadRepository.findAll();
    }
}