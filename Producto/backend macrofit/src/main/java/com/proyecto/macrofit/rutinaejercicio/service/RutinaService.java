package com.proyecto.macrofit.rutinaejercicio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.macrofit.rutinaejercicio.model.Rutina;
import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaEntity;
import com.proyecto.macrofit.rutinaejercicio.repository.RutinaRepository;

@Service
public class RutinaService {
    
    @Autowired
    private RutinaRepository repositorioRutina;

    public List<Rutina> obtenerRutinas(){
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
    

    //Conversiones
    private Rutina convertirARutina(RutinaEntity entidad){
        if (entidad == null) return null;
        
        Rutina rutina = new Rutina();
        rutina.setIdRutina(entidad.getIdRutina());
        rutina.setNombreRutina(entidad.getNombreRutina());
        rutina.setDescripcion(entidad.getDescripcion());
        rutina.setActivoCatalogo(entidad.getActivoCatalogo());
        return rutina;
    }

    private RutinaEntity convertirAEntidad(Rutina rutina){
        if (rutina == null) return null;

        RutinaEntity entidad = new RutinaEntity();
        entidad.setIdRutina(rutina.getIdRutina());
        entidad.setNombreRutina(rutina.getNombreRutina());
        entidad.setDescripcion(rutina.getDescripcion());
        entidad.setActivoCatalogo(rutina.getActivoCatalogo());
        return entidad;
    }
}
