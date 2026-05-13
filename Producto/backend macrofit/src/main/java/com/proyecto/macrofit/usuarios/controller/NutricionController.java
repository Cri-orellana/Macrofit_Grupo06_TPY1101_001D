package com.proyecto.macrofit.usuarios.controller;

import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import com.proyecto.macrofit.usuarios.model.TipoAlimentacion;
import com.proyecto.macrofit.usuarios.repository.ComidaRecomendadaRepository;
import com.proyecto.macrofit.usuarios.repository.TipoAlimentacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutricion")
public class NutricionController {

    @Autowired
    private TipoAlimentacionRepository tipoRepo;

    @Autowired
    private ComidaRecomendadaRepository comidaRepo;

    @GetMapping("/tipos-dieta")
    public List<TipoAlimentacion> obtenerTiposDieta() {
        return tipoRepo.findAll();
    }

    @GetMapping("/comidas")
    public List<ComidaRecomendada> obtenerComidas(@RequestParam(required = false) Integer tipoId) {
        if (tipoId != null) {
            return comidaRepo.findByTipoAlimentacionId(tipoId);
        }
        return comidaRepo.findAll();
    }
}