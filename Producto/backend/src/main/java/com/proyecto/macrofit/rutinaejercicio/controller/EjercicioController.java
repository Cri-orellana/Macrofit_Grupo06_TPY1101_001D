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

// --- IMPORTACIONES AGREGADAS (Del Código 1) ---
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import com.proyecto.macrofit.rutinaejercicio.service.EjercicioExcelImportService;

import com.proyecto.macrofit.rutinaejercicio.model.Ejercicio;
import com.proyecto.macrofit.rutinaejercicio.service.EjercicioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/ejercicios")
@Tag(name = "Ejercicios", description = "CRUD y consultas de ejercicios")
public class EjercicioController {

    @Autowired
    private EjercicioService servicioEjercicio;

    // --- SERVICIO AGREGADO (Del Código 1) ---
    @Autowired
    private EjercicioExcelImportService ejercicioExcelImportService;

    @GetMapping
    @Operation(summary = "Obtener todos los ejercicios")
    public ResponseEntity<List<Ejercicio>> obtenerTodos() {
        return new ResponseEntity<>(servicioEjercicio.obtenerTodosLosEjercicios(), HttpStatus.OK);
    }

    @GetMapping("/activos")
    @Operation(summary = "Obtener ejercicios activos del catálogo")
    public ResponseEntity<List<Ejercicio>> obtenerActivos() {
        return new ResponseEntity<>(servicioEjercicio.obtenerEjerciciosActivos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener ejercicio por ID")
    public ResponseEntity<Ejercicio> obtenerPorId(@PathVariable Integer id) {
        Ejercicio ejercicio = servicioEjercicio.obtenerEjercicioPorId(id);
        return ejercicio != null
                ? new ResponseEntity<>(ejercicio, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<Ejercicio>> filtrar(
            @RequestParam(required = false) String zonaMuscular,
            @RequestParam(required = false) String implemento,
            @RequestParam(required = false) String nivelDificultad,
            @RequestParam(required = false) String musculoObjetivo) {

        return new ResponseEntity<>(
                servicioEjercicio.filtrarEjercicios(
                        zonaMuscular,
                        implemento,
                        nivelDificultad,
                        musculoObjetivo),
                HttpStatus.OK);
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar ejercicios por nombre")
    public ResponseEntity<List<Ejercicio>> buscarPorNombre(@RequestParam String nombre) {
        return new ResponseEntity<>(servicioEjercicio.buscarPorNombre(nombre), HttpStatus.OK);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo ejercicio")
    public ResponseEntity<Ejercicio> crear(@RequestBody Ejercicio ejercicio) {
        return new ResponseEntity<>(servicioEjercicio.crearEjercicio(ejercicio), HttpStatus.CREATED);
    }

    // --- ENDPOINT AGREGADO (Del Código 1) ---
    @PostMapping(value = "/importar-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Importar ejercicios desde Excel")
    public ResponseEntity<?> importarExcel(@RequestParam("archivo") MultipartFile archivo) {
        try {
            if (archivo.isEmpty()) {
                return new ResponseEntity<>("El archivo está vacío.", HttpStatus.BAD_REQUEST);
            }

            int cantidad = ejercicioExcelImportService.importarEjercicios(archivo);

            return new ResponseEntity<>(
                    "Ejercicios importados correctamente: " + cantidad,
                    HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(
                    "Error al importar Excel: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un ejercicio existente")
    public ResponseEntity<Ejercicio> modificar(@PathVariable Integer id, @RequestBody Ejercicio ejercicio) {
        Ejercicio actualizado = servicioEjercicio.modificarEjercicio(id, ejercicio);
        return actualizado != null
                ? new ResponseEntity<>(actualizado, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un ejercicio")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = servicioEjercicio.eliminarEjercicio(id);
        return eliminado
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}