package com.proyecto.macrofit.rutinaejercicio.service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                .sorted(Comparator.comparing(RutinaEjercicioEntity::getOrden,
                        Comparator.nullsLast(Integer::compareTo)))
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

    //Conversiones

    private RutinaEjercicio convertirARutinaEjercicio(RutinaEjercicioEntity entidad) {
        if (entidad == null) return null;

        RutinaEjercicio rutinaEjercicio = new RutinaEjercicio();
        rutinaEjercicio.setIdRutinaEjercicio(entidad.getIdRutinaEjercicio());
        rutinaEjercicio.setIdRutina(entidad.getIdRutina());
        rutinaEjercicio.setIdEjercicio(entidad.getIdEjercicio());
        rutinaEjercicio.setOrden(entidad.getOrden());
        rutinaEjercicio.setSeries(entidad.getSeries());
        rutinaEjercicio.setRepeticiones(entidad.getRepeticiones());
        rutinaEjercicio.setTiempoSeg(entidad.getTiempoSeg());
        rutinaEjercicio.setPesoReferencia(entidad.getPesoReferencia());
        return rutinaEjercicio;
    }

    private RutinaEjercicioEntity convertirAEntidad(RutinaEjercicio rutinaEjercicio) {
        if (rutinaEjercicio == null) return null;

        RutinaEjercicioEntity entidad = new RutinaEjercicioEntity();
        entidad.setIdRutinaEjercicio(rutinaEjercicio.getIdRutinaEjercicio());
        entidad.setIdRutina(rutinaEjercicio.getIdRutina());
        entidad.setIdEjercicio(rutinaEjercicio.getIdEjercicio());
        entidad.setOrden(rutinaEjercicio.getOrden());
        entidad.setSeries(rutinaEjercicio.getSeries());
        entidad.setRepeticiones(rutinaEjercicio.getRepeticiones());
        entidad.setTiempoSeg(rutinaEjercicio.getTiempoSeg());
        entidad.setPesoReferencia(rutinaEjercicio.getPesoReferencia());
        return entidad;
    }
}