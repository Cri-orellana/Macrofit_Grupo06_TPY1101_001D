package com.proyecto.macrofit.macros.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.macrofit.macros.model.entity.ComidaHistorialEntity;
import com.proyecto.macrofit.macros.service.CierreService;

@RestController
@RequestMapping("api/v1/historialComidas")
public class HistorialController {
    private final CierreService cierreService;

    public HistorialController(CierreService cierreService) {
        this.cierreService = cierreService;
    }

    @GetMapping("/Usuario/{UserId}")
    public ResponseEntity<List<ComidaHistorialEntity>> obtenerHistorial(@PathVariable Long userId) {
        var lista = cierreService.obtenerHistorial(userId);
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

}
