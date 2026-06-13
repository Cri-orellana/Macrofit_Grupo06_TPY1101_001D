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

import com.proyecto.macrofit.macros.model.entity.ComidaNuevaEntity;
import com.proyecto.macrofit.macros.service.ComidaNuevaService;

@RestController
@RequestMapping("api/v1/comidas")
public class ComidaNuevaController {

    private final ComidaNuevaService comidaNuevaService;

    public ComidaNuevaController(ComidaNuevaService comidaNuevaService) {
        this.comidaNuevaService = comidaNuevaService;
    }

    @PostMapping
    public ResponseEntity<ComidaNuevaEntity> crearComida(@RequestBody ComidaNuevaEntity comida) {
        ComidaNuevaEntity guardada = comidaNuevaService.agregarAlimento(comida);
        return ResponseEntity.ok(guardada);
    }

    @GetMapping
    public ResponseEntity<List<ComidaNuevaEntity>> listarTodas() {
        return ResponseEntity.ok(comidaNuevaService.listarTodas());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ComidaNuevaEntity>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(comidaNuevaService.buscarPorNombre(nombre));
    }

    @GetMapping("/{code}")
    public ResponseEntity<ComidaNuevaEntity> obtenerPorCodigo(@PathVariable String code) {
        return comidaNuevaService.obtenerPorCodigo(code)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> eliminarPorCodigo(@PathVariable String code) {
        if (comidaNuevaService.eliminarPorCodigo(code)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
