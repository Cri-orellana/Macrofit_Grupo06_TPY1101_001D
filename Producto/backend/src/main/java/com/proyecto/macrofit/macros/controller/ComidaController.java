package com.proyecto.macrofit.macros.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.macrofit.macros.model.ComidaDto;//prueba
import com.proyecto.macrofit.macros.model.entity.ComidaNuevaEntity;
import com.proyecto.macrofit.macros.service.ComidaService;

@RestController
@RequestMapping("api/v1/comidasOpen")
public class ComidaController {
    private final ComidaService comidaService;

    public ComidaController(ComidaService comidaService) {
        this.comidaService = comidaService;
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ComidaDto>> buscarComida(@RequestParam String nombre) {
        var comidas = comidaService.getComidasByNombre(nombre);
        if (comidas.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comidas);
    }

    @GetMapping("/barra/{code}")
    public ResponseEntity<ComidaDto> getComidaByBarra(@PathVariable String code) {
        var comida = comidaService.getComidaByBarra(code);
        if (comida == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comida);
    }

    private ComidaDto convertirEntidadADto(ComidaNuevaEntity entidad) {
        ComidaDto dto = new ComidaDto();
        dto.setBarra(entidad.getCode());
        dto.setNombre(entidad.getNombre());

        ComidaDto.Nutriments nutriments = new ComidaDto.Nutriments();
        nutriments.setCalorias(entidad.getCalorias());
        nutriments.setProteinas(entidad.getProteinas());
        nutriments.setCarbohidratos(entidad.getCarbohidratos());
        nutriments.setGrasas(entidad.getGrasas());

        dto.setNutriments(nutriments);
        return dto;
    }

}
