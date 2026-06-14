package com.proyecto.macrofit.rutinaejercicio.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.macrofit.rutinaejercicio.model.Rutina;
import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaEjercicioEntity;
import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaEntity;
import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaUsuarioEntity;
import com.proyecto.macrofit.rutinaejercicio.repository.RutinaEjercicioRepository;
import com.proyecto.macrofit.rutinaejercicio.repository.RutinaRepository;
import com.proyecto.macrofit.rutinaejercicio.repository.RutinaUsuarioRepository;

import jakarta.transaction.Transactional;

@Service
public class RutinaService {

    @Autowired
    private RutinaRepository repositorioRutina;

    // --- REPOSITORIOS AGREGADOS (Del Código 1) ---
    @Autowired
    private RutinaUsuarioRepository repositorioRutinaUsuario;

    @Autowired
    private RutinaEjercicioRepository repositorioRutinaEjercicio;

    // --- MÉTODOS BASE (Del Código 2) ---
    public List<Rutina> obtenerRutinas() {
        return repositorioRutina.findAll().stream()
                .map(this::convertirARutina).collect(Collectors.toList());
    }

    public List<Rutina> obtenerRutinasActivasCatalogo() {
        return repositorioRutina.findAll().stream()
                .filter(r -> Boolean.TRUE.equals(r.getActivoCatalogo()))
                .map(this::convertirARutina)
                .collect(Collectors.toList());
    }

    public Rutina obtenerRutinaPorId(Integer id) {
        Optional<RutinaEntity> entidad = repositorioRutina.findById(id);
        return entidad.map(this::convertirARutina).orElse(null);
    }

    public List<Rutina> buscarRutinasPorNombre(String nombre) {
        return repositorioRutina.findAll().stream()
                .filter(r -> r.getNombreRutina() != null &&
                        r.getNombreRutina().toLowerCase().contains(nombre.toLowerCase()))
                .map(this::convertirARutina)
                .collect(Collectors.toList());
    }

    public Rutina crearRutina(Rutina rutina) {
        if (rutina.getActivoCatalogo() == null) {
            rutina.setActivoCatalogo(true);
        }

        RutinaEntity guardada = repositorioRutina.save(convertirAEntidad(rutina));
        return convertirARutina(guardada);
    }

    public Rutina modificarRutina(Integer id, Rutina rutinaActualizada) {
        return repositorioRutina.findById(id).map(entidad -> {

            if (rutinaActualizada.getNombreRutina() != null)
                entidad.setNombreRutina(rutinaActualizada.getNombreRutina());

            if (rutinaActualizada.getDescripcion() != null)
                entidad.setDescripcion(rutinaActualizada.getDescripcion());

            if (rutinaActualizada.getActivoCatalogo() != null)
                entidad.setActivoCatalogo(rutinaActualizada.getActivoCatalogo());

            RutinaEntity guardada = repositorioRutina.save(entidad);
            return convertirARutina(guardada);

        }).orElse(null);
    }

    public boolean eliminarRutina(Integer id) {
        if (repositorioRutina.existsById(id)) {
            repositorioRutina.deleteById(id);
            return true;
        }
        return false;
    }

    // --- MÉTODOS FALTANTES AGREGADOS (Del Código 1) ---

    public List<Rutina> listarRutinasPorUsuario(Integer idUsuario) {
        return repositorioRutina.findByIdUsuarioCreadorAndEsBaseFalse(idUsuario)
                .stream()
                .map(this::convertirARutina)
                .toList();
    }

    // Sobrecarga del método crearRutina para asignación a usuario
    public Rutina crearRutina(Rutina rutina, Integer idUsuario) {
        rutina.setIdRutina(null);
        rutina.setIdUsuarioCreador(idUsuario);
        rutina.setEsBase(false);
        rutina.setActivoCatalogo(false);

        RutinaEntity guardada = repositorioRutina.save(convertirAEntidad(rutina));

        RutinaUsuarioEntity asignacion = new RutinaUsuarioEntity();
        asignacion.setIdRutina(guardada.getIdRutina());
        asignacion.setIdUsuario(idUsuario);
        asignacion.setFechaInicio(LocalDate.now());
        asignacion.setActivo(true);
        repositorioRutinaUsuario.save(asignacion);

        return convertirARutina(guardada);
    }

    public Rutina crearRutinaCatalogo(Rutina rutina) {
        rutina.setIdRutina(null);
        rutina.setIdUsuarioCreador(null);
        rutina.setEsBase(true);
        rutina.setActivoCatalogo(true);

        RutinaEntity guardada = repositorioRutina.save(convertirAEntidad(rutina));
        return convertirARutina(guardada);
    }

    public Rutina editarRutinaPersonal(Integer idRutina, Integer idUsuario, Rutina rutinaEditada) {
        RutinaEntity existente = repositorioRutina.findById(idRutina)
                .orElseThrow(() -> new RuntimeException("Rutina no encontrada"));
        if (!idUsuario.equals(existente.getIdUsuarioCreador())) {
            throw new RuntimeException("No puedes editar una rutina que no te pertenece");
        }
        if (Boolean.TRUE.equals(existente.getActivoCatalogo()) || Boolean.TRUE.equals(existente.getEsBase())) {
            throw new RuntimeException("No puedes editar directamente una rutina del catálogo");
        }
        existente.setNombreRutina(rutinaEditada.getNombreRutina());
        existente.setDescripcion(rutinaEditada.getDescripcion());

        RutinaEntity guardada = repositorioRutina.save(existente);

        return convertirARutina(guardada);
    }

    @Transactional
    public Rutina copiarRutinaBaseAUsuario(Integer idRutinaBase, Integer idUsuario) {
        RutinaEntity base = repositorioRutina.findById(idRutinaBase)
                .orElseThrow(() -> new RuntimeException("Rutina base no encontrada"));
        if (!Boolean.TRUE.equals(base.getActivoCatalogo()) && !Boolean.TRUE.equals(base.getEsBase())) {
            throw new RuntimeException("La rutina no es una rutina base o de catálogo");
        }
        RutinaEntity copia = new RutinaEntity();
        copia.setIdUsuarioCreador(idUsuario);
        copia.setNombreRutina(base.getNombreRutina() + " personalizada");
        copia.setDescripcion(base.getDescripcion());
        copia.setEsBase(false);
        copia.setActivoCatalogo(false);

        RutinaEntity copiaGuardada = repositorioRutina.save(copia);
        List<RutinaEjercicioEntity> ejerciciosBase = repositorioRutinaEjercicio.findByIdRutina(idRutinaBase);
        for (RutinaEjercicioEntity ejercicioBase : ejerciciosBase) {
            RutinaEjercicioEntity nuevo = new RutinaEjercicioEntity();

            nuevo.setIdRutina(copiaGuardada.getIdRutina());
            nuevo.setIdEjercicio(ejercicioBase.getIdEjercicio());
            nuevo.setOrden(ejercicioBase.getOrden());
            nuevo.setSeries(ejercicioBase.getSeries());
            nuevo.setRepeticiones(ejercicioBase.getRepeticiones());
            nuevo.setTiempoSeg(ejercicioBase.getTiempoSeg());
            nuevo.setPesoReferencia(ejercicioBase.getPesoReferencia());

            repositorioRutinaEjercicio.save(nuevo);
        }
        return convertirARutina(copiaGuardada);
    }

    // --- CONVERSIONES ---
    // Conservan la estructura del Código 2, pero se añaden los campos del Código 1
    // estrictamente necesarios para que los métodos nuevos no den error.

    private Rutina convertirARutina(RutinaEntity entidad) {
        if (entidad == null)
            return null;

        Rutina rutina = new Rutina();
        rutina.setIdRutina(entidad.getIdRutina());
        rutina.setNombreRutina(entidad.getNombreRutina());
        rutina.setDescripcion(entidad.getDescripcion());
        rutina.setActivoCatalogo(entidad.getActivoCatalogo());

        // Campos faltantes
        rutina.setIdUsuarioCreador(entidad.getIdUsuarioCreador());
        rutina.setEsBase(entidad.getEsBase());

        return rutina;
    }

    private RutinaEntity convertirAEntidad(Rutina rutina) {
        if (rutina == null)
            return null;

        RutinaEntity entidad = new RutinaEntity();
        entidad.setIdRutina(rutina.getIdRutina());
        entidad.setNombreRutina(rutina.getNombreRutina());
        entidad.setDescripcion(rutina.getDescripcion());
        entidad.setActivoCatalogo(rutina.getActivoCatalogo());

        // Campos faltantes
        entidad.setIdUsuarioCreador(rutina.getIdUsuarioCreador());
        entidad.setEsBase(rutina.getEsBase());

        return entidad;
    }
}