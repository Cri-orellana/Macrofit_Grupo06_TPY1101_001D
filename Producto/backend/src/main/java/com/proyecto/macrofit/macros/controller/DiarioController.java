package com.proyecto.macrofit.macros.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.macrofit.macros.model.ComidaDto;
import com.proyecto.macrofit.macros.model.entity.ComidaUsuarioEntity;
import com.proyecto.macrofit.macros.service.ComidasDiarias;

@RestController
@RequestMapping("api/v1/diario")
public class DiarioController {
    private final ComidasDiarias diarioService;

    public DiarioController(ComidasDiarias diarioService) {
        this.diarioService = diarioService;
    }

    @PostMapping("/agregar")
    public ResponseEntity<ComidaUsuarioEntity> agregarAlDiario(
            @RequestBody ComidaDto comidaDto,
            @RequestParam double porcion,
            @RequestParam Long userId) {

        var nuevoRegistro = diarioService.agregarComidaDiaria(comidaDto, userId, porcion);
        if (nuevoRegistro == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(nuevoRegistro);
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<ComidaUsuarioEntity>> obtenerDiario(@PathVariable Long userId) {
        var lista = diarioService.obtenerComidasDiarias(userId);
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarDelDiario(@PathVariable Long id) {
        diarioService.eliminarComidaDiaria(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/actualizar/{id}")
    public ResponseEntity<ComidaUsuarioEntity> actualizarPorcion(
            @PathVariable Long id,
            @RequestParam Double porcion) {
        var comidaActualizada = diarioService.actualizarComidaDiaria(id, porcion);
        if (comidaActualizada == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(comidaActualizada);
    }
}
