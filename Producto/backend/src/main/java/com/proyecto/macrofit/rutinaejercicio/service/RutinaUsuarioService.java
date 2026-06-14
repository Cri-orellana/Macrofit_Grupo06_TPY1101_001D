package com.proyecto.macrofit.rutinaejercicio.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.proyecto.macrofit.rutinaejercicio.model.RutinaUsuario;
import com.proyecto.macrofit.rutinaejercicio.model.Entity.RutinaUsuarioEntity;
import com.proyecto.macrofit.rutinaejercicio.repository.RutinaUsuarioRepository;

@Service
public class RutinaUsuarioService {

    @Autowired
    private RutinaUsuarioRepository repositorioRutinaUsuario;

    public List<RutinaUsuario> obtenerTodasLasAsignaciones() {
        return repositorioRutinaUsuario.findAll().stream()
                .map(this::convertirARutinaUsuario)
                .collect(Collectors.toList());
    }

    public RutinaUsuario obtenerAsignacionPorId(Integer id) {
        Optional<RutinaUsuarioEntity> entidad = repositorioRutinaUsuario.findById(id);
        return entidad.map(this::convertirARutinaUsuario).orElse(null);
    }

    public List<RutinaUsuario> obtenerRutinasPorUsuario(Integer idUsuario) {
        return repositorioRutinaUsuario.findAll().stream()
                .filter(ru -> ru.getIdUsuario() != null && ru.getIdUsuario().equals(idUsuario))
                .map(this::convertirARutinaUsuario)
                .collect(Collectors.toList());
    }

    public List<RutinaUsuario> obtenerHistorialRutinas(Integer idUsuario) {
        return repositorioRutinaUsuario.findAll().stream()
                .filter(ru -> ru.getIdUsuario() != null && ru.getIdUsuario().equals(idUsuario))
                .filter(ru -> Boolean.FALSE.equals(ru.getActivo()))
                .map(this::convertirARutinaUsuario)
                .collect(Collectors.toList());
    }

    public RutinaUsuario obtenerRutinaActivaUsuario(Integer idUsuario) {
        return repositorioRutinaUsuario.findAll().stream()
                .filter(ru -> ru.getIdUsuario() != null && ru.getIdUsuario().equals(idUsuario))
                .filter(ru -> Boolean.TRUE.equals(ru.getActivo()))
                .map(this::convertirARutinaUsuario)
                .findFirst()
                .orElse(null);
    }

    public RutinaUsuario asignarRutinaUsuario(RutinaUsuario rutinaUsuario) {
        if (rutinaUsuario.getActivo() == null) {
            rutinaUsuario.setActivo(true);
        }

        RutinaUsuarioEntity guardada = repositorioRutinaUsuario.save(convertirAEntidad(rutinaUsuario));
        return convertirARutinaUsuario(guardada);
    }

    public RutinaUsuario modificarAsignacion(Integer id, RutinaUsuario actualizada) {
        return repositorioRutinaUsuario.findById(id).map(entidad -> {

            if (actualizada.getIdRutina() != null)
                entidad.setIdRutina(actualizada.getIdRutina());

            if (actualizada.getIdUsuario() != null)
                entidad.setIdUsuario(actualizada.getIdUsuario());

            if (actualizada.getFechaInicio() != null)
                entidad.setFechaInicio(actualizada.getFechaInicio());

            if (actualizada.getFechaFin() != null)
                entidad.setFechaFin(actualizada.getFechaFin());

            if (actualizada.getActivo() != null)
                entidad.setActivo(actualizada.getActivo());

            RutinaUsuarioEntity guardada = repositorioRutinaUsuario.save(entidad);
            return convertirARutinaUsuario(guardada);

        }).orElse(null);
    }

    public RutinaUsuario desactivarAsignacion(Integer id) {
        return repositorioRutinaUsuario.findById(id).map(entidad -> {
            entidad.setActivo(false);
            RutinaUsuarioEntity guardada = repositorioRutinaUsuario.save(entidad);
            return convertirARutinaUsuario(guardada);
        }).orElse(null);
    }

    public boolean eliminarAsignacion(Integer id) {
        if (repositorioRutinaUsuario.existsById(id)) {
            repositorioRutinaUsuario.deleteById(id);
            return true;
        }
        return false;
    }

    // Conversiones

    private RutinaUsuario convertirARutinaUsuario(RutinaUsuarioEntity entidad) {
        if (entidad == null)
            return null;

        RutinaUsuario rutinaUsuario = new RutinaUsuario();
        rutinaUsuario.setIdRutinaUsuario(entidad.getIdRutinaUsuario());
        rutinaUsuario.setIdRutina(entidad.getIdRutina());
        rutinaUsuario.setIdUsuario(entidad.getIdUsuario());
        rutinaUsuario.setFechaInicio(entidad.getFechaInicio());
        rutinaUsuario.setFechaFin(entidad.getFechaFin());
        rutinaUsuario.setActivo(entidad.getActivo());
        return rutinaUsuario;
    }

    private RutinaUsuarioEntity convertirAEntidad(RutinaUsuario rutinaUsuario) {
        if (rutinaUsuario == null)
            return null;

        RutinaUsuarioEntity entidad = new RutinaUsuarioEntity();
        entidad.setIdRutinaUsuario(rutinaUsuario.getIdRutinaUsuario());
        entidad.setIdRutina(rutinaUsuario.getIdRutina());
        entidad.setIdUsuario(rutinaUsuario.getIdUsuario());
        entidad.setFechaInicio(rutinaUsuario.getFechaInicio());
        entidad.setFechaFin(rutinaUsuario.getFechaFin());
        entidad.setActivo(rutinaUsuario.getActivo());
        return entidad;
    }
}
