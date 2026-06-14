package com.proyecto.macrofit.rutinaejercicio.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.macrofit.rutinaejercicio.model.RutinaUsuario;
import com.proyecto.macrofit.rutinaejercicio.service.RutinaUsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/rutina-usuario")
@Tag(name = "RutinaUsuario", description = "Asignación de rutinas a usuarios + historial")
public class RutinaUsuarioController {

    @Autowired
    private RutinaUsuarioService servicioRutinaUsuario;

    @GetMapping
    @Operation(summary = "Obtener todas las asignaciones de rutinas")
    public ResponseEntity<List<RutinaUsuario>> obtenerTodas() {
        return new ResponseEntity<>(servicioRutinaUsuario.obtenerTodasLasAsignaciones(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener asignación por ID")
    public ResponseEntity<RutinaUsuario> obtenerPorId(@PathVariable Integer id) {
        RutinaUsuario rutinaUsuario = servicioRutinaUsuario.obtenerAsignacionPorId(id);
        return rutinaUsuario != null
                ? new ResponseEntity<>(rutinaUsuario, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Obtener todas las rutinas asignadas a un usuario")
    public ResponseEntity<List<RutinaUsuario>> obtenerPorUsuario(@PathVariable Integer idUsuario) {
        return new ResponseEntity<>(servicioRutinaUsuario.obtenerRutinasPorUsuario(idUsuario), HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/historial")
    @Operation(summary = "Obtener historial de rutinas de un usuario")
    public ResponseEntity<List<RutinaUsuario>> obtenerHistorial(@PathVariable Integer idUsuario) {
        return new ResponseEntity<>(servicioRutinaUsuario.obtenerHistorialRutinas(idUsuario), HttpStatus.OK);
    }

    @GetMapping("/usuario/{idUsuario}/activa")
    @Operation(summary = "Obtener la rutina activa de un usuario")
    public ResponseEntity<RutinaUsuario> obtenerActiva(@PathVariable Integer idUsuario) {
        RutinaUsuario rutinaActiva = servicioRutinaUsuario.obtenerRutinaActivaUsuario(idUsuario);
        return rutinaActiva != null
                ? new ResponseEntity<>(rutinaActiva, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping
    @Operation(summary = "Asignar rutina a un usuario")
    public ResponseEntity<RutinaUsuario> asignar(@RequestBody RutinaUsuario rutinaUsuario) {
        return new ResponseEntity<>(servicioRutinaUsuario.asignarRutinaUsuario(rutinaUsuario), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar asignación de rutina a usuario")
    public ResponseEntity<RutinaUsuario> modificar(@PathVariable Integer id,
            @RequestBody RutinaUsuario rutinaUsuario) {
        RutinaUsuario actualizada = servicioRutinaUsuario.modificarAsignacion(id, rutinaUsuario);
        return actualizada != null
                ? new ResponseEntity<>(actualizada, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}/desactivar")
    @Operation(summary = "Desactivar una asignación de rutina")
    public ResponseEntity<RutinaUsuario> desactivar(@PathVariable Integer id) {
        RutinaUsuario desactivada = servicioRutinaUsuario.desactivarAsignacion(id);
        return desactivada != null
                ? new ResponseEntity<>(desactivada, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una asignación de rutina")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminada = servicioRutinaUsuario.eliminarAsignacion(id);
        return eliminada
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
