package com.proyecto.macrofit.usuarios.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.proyecto.macrofit.usuarios.model.LoginResponse;
import com.proyecto.macrofit.usuarios.model.Usuario;
import com.proyecto.macrofit.usuarios.service.UsuarioService;
import com.proyecto.macrofit.usuarios.model.LoginRequest;
import com.proyecto.macrofit.usuarios.security.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Administracion Usuarios", description = "CRUD Usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/registro")
    @Operation(summary = "Registra un nuevo Usuario")
    public ResponseEntity<?> registrar(@RequestBody Usuario usuario) {
        try {
            Usuario nuevo = usuarioService.crearUsuario(usuario);
            return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Login — devuelve JWT + datos del usuario")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Usuario user = usuarioService.loginUsuario(request.getCorreo(), request.getContrasena());

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
        }

        String token = jwtUtil.generarToken(user.getCorreo(), user.getRol());

        LoginResponse respuesta = new LoginResponse(
                token,
                user.getId_usuario(),
                user.getNom_usuario(),
                user.getCorreo(),
                user.getRol());

        return ResponseEntity.ok(respuesta);
    }

    @GetMapping
    @Operation(summary = "Usuarios Registrados (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<Usuario>> obtenerTodos() {
        return new ResponseEntity<>(usuarioService.obtenerTodosLosUsuarios(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener por ID", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Usuario> obtenerPorId(@PathVariable Integer id) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(id);
        return usuario != null
                ? new ResponseEntity<>(usuario, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar un usuario existente (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Usuario> modificar(@PathVariable Integer id, @RequestBody Usuario usuario) {
        Usuario actualizado = usuarioService.modificarUsuario(id, usuario);
        return actualizado != null
                ? new ResponseEntity<>(actualizado, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un usuario (solo ADMIN)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        boolean eliminado = usuarioService.eliminarUsuario(id);
        return eliminado
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PatchMapping("/{id}/perfil")
    @Operation(summary = "Actualiza métricas del perfil", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Usuario> actualizarPerfil(
            @PathVariable Integer id,
            @RequestBody Usuario datosActualizados) {

        Usuario actualizado = usuarioService.actualizarPerfil(id, datosActualizados);
        return actualizado != null
                ? new ResponseEntity<>(actualizado, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}