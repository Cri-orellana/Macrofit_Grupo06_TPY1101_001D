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
    private UsuarioService usuarioService;

    @GetMapping
    @Operation(summary = "Usuarios Registrados")
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return new ResponseEntity<>(usuarioService.obtenerTodosLosUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por ID")
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario != null ? new ResponseEntity<>(usuario, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/registro")
    @Operation(summary = "Registra un nuevo Usuario")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            // Intenta crear el usuario
            Usuario nuevo = usuarioService.crearUsuario(usuario);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Si salta alguna de nuestras validaciones (correo repetido o contraseña
            // corta), devolvemos el error a Android
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("DEBUG MÓVIL: Intentando login con correo: " + request.getCorreo());
        Usuario user = usuarioService.loginUsuario(request.getCorreo(), request.getContrasena());

        if (user != null) {
            System.out.println("DEBUG MÓVIL: ¡ÉXITO! Usuario encontrado: " + user.getNom_usuario());
            return ResponseEntity.ok(user);
        } else {
            System.out.println("DEBUG MÓVIL: FALLIDO. Revisar hash de contraseña.");
            return ResponseEntity.status(401).body("Credenciales incorrectas");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un usuario existente")
    public ResponseEntity<Usuario> modificar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.modificarUsuario(id, usuario);
        return actualizado != null ? new ResponseEntity<>(actualizado, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        return eliminado ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // El patch permite cambios parciales, no requiere todos los campos
    @PatchMapping("/{id}/perfil")
    @Operation(summary = "Actualiza métricas del perfil y recalcula el plan nutricional")
    public ResponseEntity<Usuario> actualizarPerfil(
            @PathVariable Integer id,
            @RequestBody Usuario datosActualizados) {

        Usuario actualizado = usuarioService.actualizarPerfil(id, datosActualizados);

        if (actualizado != null) {
            return new ResponseEntity<>(actualizado, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}