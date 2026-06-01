package com.proyecto.macrofit.usuarios.controller;

import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import com.proyecto.macrofit.usuarios.model.TipoAlimentacion;
import com.proyecto.macrofit.usuarios.service.SpoonacularService;
import com.proyecto.macrofit.usuarios.repository.ComidaRecomendadaRepository;
import com.proyecto.macrofit.usuarios.repository.TipoAlimentacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nutricion")
public class NutricionController {

    @Autowired
    private TipoAlimentacionRepository tipoRepo;

    @Autowired
    private ComidaRecomendadaRepository comidaRepo;

    @Autowired
    private SpoonacularService spoonacularService;

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

    @GetMapping("/recomendaciones")
    public ResponseEntity<List<ComidaRecomendada>> obtenerRecomendaciones(
            @RequestParam(required = false, defaultValue = "") String tipoDieta,
            @RequestParam(required = false, defaultValue = "") String ingredientes,
            @RequestParam(required = false, defaultValue = "1000") Float maxCarbohidratos,
            @RequestParam(required = false, defaultValue = "0") Float minProteina,
            @RequestParam(required = false, defaultValue = "1000") Float maxGrasa) {

        List<ComidaRecomendada> recomendaciones = spoonacularService.buscarRecetasPersonalizadas(
                tipoDieta,
                ingredientes,
                maxCarbohidratos,
                minProteina,
                maxGrasa);

        return ResponseEntity.ok(recomendaciones);
    }
}