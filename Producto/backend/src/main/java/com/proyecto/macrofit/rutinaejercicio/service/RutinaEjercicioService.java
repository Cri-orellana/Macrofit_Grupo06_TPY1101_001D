package com.proyecto.macrofit.rutinaejercicio.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.proyecto.macrofit.rutinaejercicio.model.RutinaEjercicio;
import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaEjercicioEntity;
import com.proyecto.macrofit.rutinaejercicio.repository.RutinaEjercicioRepository;

@Service
public class RutinaEjercicioService {

    @Autowired
    private RutinaEjercicioRepository repositorioRutinaEjercicio;

    public List<RutinaEjercicio> obtenerTodos() {
        return repositorioRutinaEjercicio.findAll().stream()
                .map(this::convertirARutinaEjercicio)
                .collect(Collectors.toList());
    }

    public RutinaEjercicio obtenerPorId(Integer id) {
        Optional<RutinaEjercicioEntity> entidad = repositorioRutinaEjercicio.findById(id);
        return entidad.map(this::convertirARutinaEjercicio).orElse(null);
    }

    public List<RutinaEjercicio> obtenerPorRutina(Integer idRutina) {
        return repositorioRutinaEjercicio.findAll().stream()
                .filter(re -> re.getIdRutina() != null && re.getIdRutina().equals(idRutina))
                // Ordenamos primero por día y luego por orden
                .sorted(Comparator.comparing((RutinaEjercicioEntity e) -> e.getDia() == null ? 1 : e.getDia())
                        .thenComparing(e -> e.getOrden() == null ? 99 : e.getOrden()))
                .map(this::convertirARutinaEjercicio)
                .collect(Collectors.toList());
    }

    public RutinaEjercicio crearRutinaEjercicio(RutinaEjercicio rutinaEjercicio) {
        RutinaEjercicioEntity guardada = repositorioRutinaEjercicio.save(convertirAEntidad(rutinaEjercicio));
        return convertirARutinaEjercicio(guardada);
    }

    public RutinaEjercicio modificarRutinaEjercicio(Integer id, RutinaEjercicio actualizado) {
        return repositorioRutinaEjercicio.findById(id).map(entidad -> {

            if (actualizado.getIdRutina() != null)
                entidad.setIdRutina(actualizado.getIdRutina());

            if (actualizado.getIdEjercicio() != null)
                entidad.setIdEjercicio(actualizado.getIdEjercicio());

            if (actualizado.getDia() != null)
                entidad.setDia(actualizado.getDia()); // NUEVO

            if (actualizado.getOrden() != null)
                entidad.setOrden(actualizado.getOrden());

            if (actualizado.getSeries() != null)
                entidad.setSeries(actualizado.getSeries());

            if (actualizado.getRepeticiones() != null)
                entidad.setRepeticiones(actualizado.getRepeticiones());

            if (actualizado.getTiempoSeg() != null)
                entidad.setTiempoSeg(actualizado.getTiempoSeg());

            if (actualizado.getPesoReferencia() != null)
                entidad.setPesoReferencia(actualizado.getPesoReferencia());

            RutinaEjercicioEntity guardada = repositorioRutinaEjercicio.save(entidad);
            return convertirARutinaEjercicio(guardada);

        }).orElse(null);
    }

    // --- REEMPLAZAR EJERCICIOS DE RUTINA (Del Código 1) ---
    @Transactional
    public List<RutinaEjercicio> reemplazarEjerciciosDeRutina(Integer idRutina, List<RutinaEjercicio> ejercicios) {
        repositorioRutinaEjercicio.deleteByIdRutina(idRutina);
        return ejercicios.stream()
                .map(e -> {
                    e.setIdRutina(idRutina);
                    return convertirARutinaEjercicio(
                            repositorioRutinaEjercicio.save(convertirAEntidad(e)));
                })
                .toList();
    }

    // --- DRAG AND DROP (Del Código 2) ---
    @Transactional
    public List<RutinaEjercicio> actualizarMultiples(List<RutinaEjercicio> rutinasActualizadas) {
        return rutinasActualizadas.stream().map(act -> modificarRutinaEjercicio(act.getIdRutinaEjercicio(), act))
                .collect(Collectors.toList());
    }

    public boolean eliminarRutinaEjercicio(Integer id) {
        if (repositorioRutinaEjercicio.existsById(id)) {
            repositorioRutinaEjercicio.deleteById(id);
            return true;
        }
        return false;
    }

    public int eliminarPorRutina(Integer idRutina) {
        List<RutinaEjercicioEntity> lista = repositorioRutinaEjercicio.findAll().stream()
                .filter(re -> re.getIdRutina() != null && re.getIdRutina().equals(idRutina))
                .collect(Collectors.toList());

        repositorioRutinaEjercicio.deleteAll(lista);
        return lista.size();
    }

    // Conversiones

    private RutinaEjercicio convertirARutinaEjercicio(RutinaEjercicioEntity entidad) {
        if (entidad == null)
            return null;

        RutinaEjercicio rutinaEjercicio = new RutinaEjercicio();
        rutinaEjercicio.setIdRutinaEjercicio(entidad.getIdRutinaEjercicio());
        rutinaEjercicio.setIdRutina(entidad.getIdRutina());
        rutinaEjercicio.setIdEjercicio(entidad.getIdEjercicio());
        rutinaEjercicio.setDia(entidad.getDia() != null ? entidad.getDia() : 1);
        rutinaEjercicio.setOrden(entidad.getOrden());
        rutinaEjercicio.setSeries(entidad.getSeries());
        rutinaEjercicio.setRepeticiones(entidad.getRepeticiones());
        rutinaEjercicio.setTiempoSeg(entidad.getTiempoSeg());
        rutinaEjercicio.setPesoReferencia(entidad.getPesoReferencia());
        return rutinaEjercicio;
    }

    private RutinaEjercicioEntity convertirAEntidad(RutinaEjercicio rutinaEjercicio) {
        if (rutinaEjercicio == null)
            return null;

        RutinaEjercicioEntity entidad = new RutinaEjercicioEntity();
        entidad.setIdRutinaEjercicio(rutinaEjercicio.getIdRutinaEjercicio());
        entidad.setIdRutina(rutinaEjercicio.getIdRutina());
        entidad.setIdEjercicio(rutinaEjercicio.getIdEjercicio());
        entidad.setDia(rutinaEjercicio.getDia() != null ? rutinaEjercicio.getDia() : 1);
        entidad.setOrden(rutinaEjercicio.getOrden());
        entidad.setSeries(rutinaEjercicio.getSeries());
        entidad.setRepeticiones(rutinaEjercicio.getRepeticiones());
        entidad.setTiempoSeg(rutinaEjercicio.getTiempoSeg());
        entidad.setPesoReferencia(rutinaEjercicio.getPesoReferencia());
        return entidad;
    }
}