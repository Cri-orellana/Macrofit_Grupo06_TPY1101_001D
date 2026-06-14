package com.proyecto.macrofit.usuarios.controller;

import com.proyecto.macrofit.usuarios.model.ComidaRecomendada;
import com.proyecto.macrofit.usuarios.model.RecetaCache;
import com.proyecto.macrofit.usuarios.model.TipoAlimentacion;
import com.proyecto.macrofit.usuarios.service.SpoonacularService;
import com.proyecto.macrofit.usuarios.repository.ComidaRecomendadaRepository;
import com.proyecto.macrofit.usuarios.repository.RecetaCacheRepository;
import com.proyecto.macrofit.usuarios.repository.TipoAlimentacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/nutricion")
public class NutricionController {

    @Autowired
    private TipoAlimentacionRepository tipoRepo;

    @Autowired
    private ComidaRecomendadaRepository comidaRepo;

    @Autowired
    private SpoonacularService spoonacularService;

    // ── AGREGADO ──
    @Autowired
    private RecetaCacheRepository recetaCacheRepo;

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

    @PostMapping("/comidas")
    public ResponseEntity<ComidaRecomendada> crearComida(@RequestBody ComidaRecomendada comida) {
        if (comida.getTipo_alimentacion() != null && comida.getTipo_alimentacion().getId_tipo_alimentacion() != null) {
            TipoAlimentacion tipo = tipoRepo.findById(comida.getTipo_alimentacion().getId_tipo_alimentacion())
                    .orElse(null);
            comida.setTipo_alimentacion(tipo);
        }
        return new ResponseEntity<>(comidaRepo.save(comida), HttpStatus.CREATED);
    }

    @PutMapping("/comidas/{id}")
    public ResponseEntity<ComidaRecomendada> editarComida(@PathVariable Integer id,
            @RequestBody ComidaRecomendada comida) {
        Optional<ComidaRecomendada> existente = comidaRepo.findById(id);
        if (existente.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        ComidaRecomendada c = existente.get();
        c.setNombre_comida(comida.getNombre_comida());
        c.setDescripcion_comida(comida.getDescripcion_comida());
        c.setCantidad_porcion(comida.getCantidad_porcion());
        c.setCalorias_porcion(comida.getCalorias_porcion());
        c.setProteina_porcion(comida.getProteina_porcion());
        c.setCarbohidratos_porcion(comida.getCarbohidratos_porcion());
        c.setGrasa_porcion(comida.getGrasa_porcion());
        if (comida.getTipo_alimentacion() != null && comida.getTipo_alimentacion().getId_tipo_alimentacion() != null) {
            TipoAlimentacion tipo = tipoRepo.findById(comida.getTipo_alimentacion().getId_tipo_alimentacion())
                    .orElse(null);
            c.setTipo_alimentacion(tipo);
        }
        return new ResponseEntity<>(comidaRepo.save(c), HttpStatus.OK);
    }

    @DeleteMapping("/comidas/{id}")
    public ResponseEntity<Void> eliminarComida(@PathVariable Integer id) {
        if (!comidaRepo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        comidaRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/recomendaciones")
    public ResponseEntity<List<ComidaRecomendada>> obtenerRecomendaciones(
            @RequestParam(required = false, defaultValue = "") String tipoDieta,
            @RequestParam(required = false, defaultValue = "") String ingredientes,
            @RequestParam(required = false, defaultValue = "1000") Float maxCarbohidratos,
            @RequestParam(required = false, defaultValue = "0") Float minProteina,
            @RequestParam(required = false, defaultValue = "1000") Float maxGrasa) {

        List<ComidaRecomendada> recomendaciones = spoonacularService.buscarRecetasPersonalizadas(
                tipoDieta, ingredientes, maxCarbohidratos, minProteina, maxGrasa);

        return ResponseEntity.ok(recomendaciones);
    }

    @PostMapping("/admin/retraducir-cache")
    public ResponseEntity<String> retraducirCache() {
        System.out.println("🔄 Iniciando re-traducción de recetas en caché...");
        int cantidad = spoonacularService.retraducirRecetasEnIngles();
        String mensaje = "✅ Re-traducción completada. Recetas actualizadas: " + cantidad;
        System.out.println(mensaje);
        return ResponseEntity.ok(mensaje);
    }

    @GetMapping("/recetas-cache")
    public ResponseEntity<List<RecetaCache>> obtenerRecetasCache() {
        return ResponseEntity.ok(recetaCacheRepo.findAll());
    }

    @DeleteMapping("/recetas-cache/{id}")
    public ResponseEntity<Void> eliminarRecetaCache(@PathVariable Integer id) {
        if (!recetaCacheRepo.existsById(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        recetaCacheRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/recetas-cache/{id}")
    public ResponseEntity<RecetaCache> editarRecetaCache(@PathVariable Integer id,
            @RequestBody RecetaCache datos) {
        Optional<RecetaCache> existente = recetaCacheRepo.findById(id);
        if (existente.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        RecetaCache r = existente.get();
        r.setNombre_comida(datos.getNombre_comida());
        r.setDescripcion_comida(datos.getDescripcion_comida());
        r.setCantidad_porcion(datos.getCantidad_porcion());
        r.setCalorias_porcion(datos.getCalorias_porcion());
        r.setProteina_porcion(datos.getProteina_porcion());
        r.setCarbohidratos_porcion(datos.getCarbohidratos_porcion());
        r.setGrasa_porcion(datos.getGrasa_porcion());
        r.setFoto_comida(datos.getFoto_comida());
        return ResponseEntity.ok(recetaCacheRepo.save(r));
    }
}