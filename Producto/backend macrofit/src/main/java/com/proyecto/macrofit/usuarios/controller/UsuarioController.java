package com.proyecto.macrofit.usuarios.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.macrofit.usuarios.model.Usuario;
import com.proyecto.macrofit.usuarios.service.UsuarioService;
import com.proyecto.macrofit.usuarios.model.LoginRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Administracion Usuarios", description = "CRUD Usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService servicioUsuario;

    @GetMapping
    @Operation(summary = "Usuarios Registrados")
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return new ResponseEntity<>(servicioUsuario.obtenerTodosLosUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por ID")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        Usuario usuario = servicioUsuario.obtenerUsuarioPorId(id);
        return usuario != null ? new ResponseEntity<>(usuario, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/registro")
    @Operation(summary = "Registra un nuevo Usuario")
    public ResponseEntity<Usuario> registrar(@RequestBody Usuario usuario) {
        return new ResponseEntity<>(servicioUsuario.crearUsuario(usuario), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Inicia sesión y valida credenciales")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest credenciales) {

        Usuario usuarioLogueado = servicioUsuario.loginUsuario(
                credenciales.getCorreo(),
                credenciales.getContrasena());

        if (usuarioLogueado != null) {
            // Devuelve un HTTP 200 (OK) y los datos del usuario
            return new ResponseEntity<>(usuarioLogueado, HttpStatus.OK);
        } else {
            // Devuelve un HTTP 401 (No autorizado) si falló
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un usuario existente")
    public ResponseEntity<Usuario> modificar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        Usuario actualizado = servicioUsuario.modificarUsuario(id, usuario);
        return actualizado != null ? new ResponseEntity<>(actualizado, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = servicioUsuario.eliminarUsuario(id);
        return eliminado ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // El patch permite cambios parciales, no requiere todos los campos
    @PatchMapping("/{id}/perfil")
    @Operation(summary = "Actualiza métricas del perfil y recalcula el plan nutricional")
    public ResponseEntity<Usuario> actualizarPerfil(
            @PathVariable Integer id,
            @RequestBody Usuario datosActualizados) {

        Usuario actualizado = servicioUsuario.actualizarPerfil(id, datosActualizados);

        if (actualizado != null) {
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}