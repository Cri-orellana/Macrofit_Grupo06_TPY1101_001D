package com.proyecto.macrofit.rutinaejercicio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.macrofit.rutinaejercicio.model.Rutina;
import com.proyecto.macrofit.rutinaejercicio.service.RutinaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/rutinas")
@Tag(name = "Rutinas", description = "CRUD y consultas de rutinas")
public class RutinaController {

    @Autowired
    private RutinaService servicioRutina;

    @GetMapping
    @Operation(summary = "Obtener todas las rutinas")
    public ResponseEntity<List<Rutina>> obtenerTodas() {
        return new ResponseEntity<>(servicioRutina.obtenerRutinas(), HttpStatus.OK);
    }

    @GetMapping("/activas")
    @Operation(summary = "Obtener rutinas activas del catálogo")
    public ResponseEntity<List<Rutina>> obtenerActivas() {
        return new ResponseEntity<>(servicioRutina.obtenerRutinasActivasCatalogo(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener rutina por ID")
    public ResponseEntity<Rutina> obtenerPorId(@PathVariable Integer id) {
        Rutina rutina = servicioRutina.obtenerRutinaPorId(id);
        return rutina != null
                ? new ResponseEntity<>(rutina, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar rutinas por nombre")
    public ResponseEntity<List<Rutina>> buscarPorNombre(@RequestParam String nombre) {
        return new ResponseEntity<>(servicioRutina.buscarRutinasPorNombre(nombre), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Crear una nueva rutina")
    public ResponseEntity<Rutina> crear(@RequestBody Rutina rutina) {
        return new ResponseEntity<>(servicioRutina.crearRutina(rutina), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar una rutina existente")
    public ResponseEntity<Rutina> modificar(@PathVariable Integer id, @RequestBody Rutina rutina) {
        Rutina actualizada = servicioRutina.modificarRutina(id, rutina);
        return actualizada != null
                ? new ResponseEntity<>(actualizada, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una rutina")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminada = servicioRutina.eliminarRutina(id);
        return eliminada
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    
}
