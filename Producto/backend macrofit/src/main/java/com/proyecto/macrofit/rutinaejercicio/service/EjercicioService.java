package com.proyecto.macrofit.rutinaejercicio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.macrofit.rutinaejercicio.model.Ejercicio;
import com.proyecto.macrofit.rutinaejercicio.model.Entity.EjercicioEntity;
import com.proyecto.macrofit.rutinaejercicio.repository.EjercicioRepository;

@Service
public class EjercicioService {
    
    @Autowired
    private EjercicioRepository repositorioEjercicio;

    public List<Ejercicio> obtenerTodosLosEjercicios() {
        return repositorioEjercicio.findAll().stream()
                .map(this::convertirAEjercicio)
                .collect(Collectors.toList());
    }

    public List<Ejercicio> obtenerEjerciciosActivos() {
        return repositorioEjercicio.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getActivoCatalogo()))
                .map(this::convertirAEjercicio)
                .collect(Collectors.toList());
    }

    public Ejercicio obtenerEjercicioPorId(Integer id) {
        Optional<EjercicioEntity> entidad = repositorioEjercicio.findById(id);
        return entidad.map(this::convertirAEjercicio).orElse(null);
    }


    public List<Ejercicio> buscarPorNombre(String nombre) {
        return repositorioEjercicio.findAll().stream()
                .filter(e -> e.getNombreEjercicio() != null &&
                        e.getNombreEjercicio().toLowerCase().contains(nombre.toLowerCase()))
                .map(this::convertirAEjercicio)
                .collect(Collectors.toList());
    }

    public Ejercicio crearEjercicio(Ejercicio ejercicio) {

        ejercicio.setIdEjercicio(null);

        if (ejercicio.getActivoCatalogo() == null) {
            ejercicio.setActivoCatalogo(true);
        }

        EjercicioEntity guardado = repositorioEjercicio.save(convertirAEntidad(ejercicio));
        return convertirAEjercicio(guardado);
    }

    public Ejercicio modificarEjercicio(Integer id, Ejercicio ejercicioActualizado) {
        return repositorioEjercicio.findById(id).map(entidad -> {

            if (ejercicioActualizado.getNombreEjercicio() != null)
                entidad.setNombreEjercicio(ejercicioActualizado.getNombreEjercicio());

            if (ejercicioActualizado.getDescripcion() != null)
                entidad.setDescripcion(ejercicioActualizado.getDescripcion());

            if (ejercicioActualizado.getZonaMuscular() != null)
                entidad.setZonaMuscular(ejercicioActualizado.getZonaMuscular());

            if (ejercicioActualizado.getImplemento() != null)
                entidad.setImplemento(ejercicioActualizado.getImplemento());

            if (ejercicioActualizado.getNivelDificultad() != null)
                entidad.setNivelDificultad(ejercicioActualizado.getNivelDificultad());
            
            if (ejercicioActualizado.getMusculoObjetivo() != null)
                entidad.setMusculoObjetivo(ejercicioActualizado.getMusculoObjetivo());            

            if (ejercicioActualizado.getImagenEjercicio() != null)
                entidad.setImagenEjercicio(ejercicioActualizado.getImagenEjercicio());

            if (ejercicioActualizado.getVideoEjercicio() != null)
                entidad.setVideoEjercicio(ejercicioActualizado.getVideoEjercicio());

            if (ejercicioActualizado.getActivoCatalogo() != null)
                entidad.setActivoCatalogo(ejercicioActualizado.getActivoCatalogo());

            EjercicioEntity guardado = repositorioEjercicio.save(entidad);
            return convertirAEjercicio(guardado);

        }).orElse(null);
    }

    public boolean eliminarEjercicio(Integer id) {
        if (repositorioEjercicio.existsById(id)) {
            repositorioEjercicio.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Ejercicio> filtrarEjercicios(
        String zonaMuscular,
        String implemento,
        String nivelDificultad,
        String musculoObjetivo) {

        return repositorioEjercicio.findAll().stream()
                .filter(e -> Boolean.TRUE.equals(e.getActivoCatalogo()))
                .filter(e -> zonaMuscular == null || zonaMuscular.isBlank()
                        || e.getZonaMuscular().equalsIgnoreCase(zonaMuscular))
                .filter(e -> implemento == null || implemento.isBlank()
                        || e.getImplemento().equalsIgnoreCase(implemento))
                .filter(e -> nivelDificultad == null || nivelDificultad.isBlank()
                        || e.getNivelDificultad().equalsIgnoreCase(nivelDificultad))
                .filter(e -> musculoObjetivo == null || musculoObjetivo.isBlank()
                        || e.getMusculoObjetivo().equalsIgnoreCase(musculoObjetivo))
                .map(this::convertirAEjercicio)
                .collect(Collectors.toList());
    }

    //Conversiones

    private Ejercicio convertirAEjercicio(EjercicioEntity entidad) {
        if (entidad == null) return null;

        Ejercicio ejercicio = new Ejercicio();
        ejercicio.setIdEjercicio(entidad.getIdEjercicio());
        ejercicio.setNombreEjercicio(entidad.getNombreEjercicio());
        ejercicio.setDescripcion(entidad.getDescripcion());
        ejercicio.setZonaMuscular(entidad.getZonaMuscular());
        ejercicio.setImplemento(entidad.getImplemento());
        ejercicio.setNivelDificultad(entidad.getNivelDificultad());
        ejercicio.setMusculoObjetivo(entidad.getMusculoObjetivo());
        ejercicio.setImagenEjercicio(entidad.getImagenEjercicio());
        ejercicio.setVideoEjercicio(entidad.getVideoEjercicio());
        ejercicio.setActivoCatalogo(entidad.getActivoCatalogo());
        return ejercicio;
    }

    private EjercicioEntity convertirAEntidad(Ejercicio ejercicio) {
        if (ejercicio == null) return null;

        EjercicioEntity entidad = new EjercicioEntity();
        entidad.setIdEjercicio(ejercicio.getIdEjercicio());
        entidad.setNombreEjercicio(ejercicio.getNombreEjercicio());
        entidad.setDescripcion(ejercicio.getDescripcion());
        entidad.setZonaMuscular(ejercicio.getZonaMuscular());
        entidad.setImplemento(ejercicio.getImplemento());
        entidad.setNivelDificultad(ejercicio.getNivelDificultad());
        entidad.setMusculoObjetivo(ejercicio.getMusculoObjetivo());
        entidad.setImagenEjercicio(ejercicio.getImagenEjercicio());
        entidad.setVideoEjercicio(ejercicio.getVideoEjercicio());
        entidad.setActivoCatalogo(ejercicio.getActivoCatalogo());
        return entidad;
    }
    
}
