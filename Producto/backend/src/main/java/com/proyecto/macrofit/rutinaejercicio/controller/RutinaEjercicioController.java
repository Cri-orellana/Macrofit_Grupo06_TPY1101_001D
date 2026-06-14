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
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.macrofit.rutinaejercicio.model.RutinaEjercicio;
import com.proyecto.macrofit.rutinaejercicio.service.RutinaEjercicioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/rutina-ejercicio")
@Tag(name = "RutinaEjercicio", description = "Gestion de ejercicios dentro de una rutina")
public class RutinaEjercicioController {

    @Autowired
    private RutinaEjercicioService servicioRutinaEjercicio;

    @GetMapping
    @Operation(summary = "Obtener todas las relaciones rutina-ejercicio")
    public ResponseEntity<List<RutinaEjercicio>> obtenerTodos() {
        return new ResponseEntity<>(servicioRutinaEjercicio.obtenerTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener relación rutina-ejercicio por ID")
    public ResponseEntity<RutinaEjercicio> obtenerPorId(@PathVariable Integer id) {
        RutinaEjercicio rutinaEjercicio = servicioRutinaEjercicio.obtenerPorId(id);
        return rutinaEjercicio != null
                ? new ResponseEntity<>(rutinaEjercicio, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/rutina/{idRutina}")
    @Operation(summary = "Obtener ejercicios de una rutina")
    public ResponseEntity<List<RutinaEjercicio>> obtenerPorRutina(@PathVariable Integer idRutina) {
        return new ResponseEntity<>(servicioRutinaEjercicio.obtenerPorRutina(idRutina), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Agregar un ejercicio a una rutina")
    public ResponseEntity<RutinaEjercicio> crear(@RequestBody RutinaEjercicio rutinaEjercicio) {
        return new ResponseEntity<>(servicioRutinaEjercicio.crearRutinaEjercicio(rutinaEjercicio), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar una relación rutina-ejercicio")
    public ResponseEntity<RutinaEjercicio> modificar(@PathVariable Integer id,
            @RequestBody RutinaEjercicio rutinaEjercicio) {
        RutinaEjercicio actualizado = servicioRutinaEjercicio.modificarRutinaEjercicio(id, rutinaEjercicio);
        return actualizado != null
                ? new ResponseEntity<>(actualizado, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/bulk")
    @Operation(summary = "Modificar multiples ejercicios a la vez (Drag & Drop)")
    public ResponseEntity<List<RutinaEjercicio>> modificarMultiples(
            @RequestBody List<RutinaEjercicio> rutinasEjercicios) {
        return new ResponseEntity<>(servicioRutinaEjercicio.actualizarMultiples(rutinasEjercicios), HttpStatus.OK);
    }

    @PutMapping("/rutina/{idRutina}/reemplazar")
    @Operation(summary = "Reemplazar ejercicios de una rutina")
    public ResponseEntity<List<RutinaEjercicio>> reemplazarEjerciciosDeRutina(
            @PathVariable Integer idRutina,
            @RequestBody List<RutinaEjercicio> ejercicios) {
        return ResponseEntity.ok(
                servicioRutinaEjercicio.reemplazarEjerciciosDeRutina(idRutina, ejercicios));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una relación rutina-ejercicio")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = servicioRutinaEjercicio.eliminarRutinaEjercicio(id);
        return eliminado
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/rutina/{idRutina}")
    @Operation(summary = "Eliminar todos los ejercicios de una rutina")
    public ResponseEntity<Void> eliminarPorRutina(@PathVariable Integer idRutina) {
        int eliminados = servicioRutinaEjercicio.eliminarPorRutina(idRutina);
        return eliminados > 0
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

}