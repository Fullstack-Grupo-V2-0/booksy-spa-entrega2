package com.example.msusuarios.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {

        log.info("GET /api/v1/usuarios");

        return ResponseEntity.ok(usuarioService.getUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable int id) {

        log.info("GET /api/v1/usuarios/{}", id);

        return ResponseEntity.ok(usuarioService.getUsuarioById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuario> getUsuarioByEmail(
            @PathVariable String email) {

        log.info("GET /api/v1/usuarios/email/{}", email);

        return ResponseEntity.ok(
                usuarioService.getUsuarioByEmail(email));
    }

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(
            @Valid @RequestBody Usuario usuario) {

        log.info("POST /api/v1/usuarios - email={}",
                usuario.getEmail());

        Usuario nuevo = usuarioService.saveUsuario(usuario);

        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(
            @PathVariable int id,
            @Valid @RequestBody Usuario usuario) {

        log.info("PUT /api/v1/usuarios/{}", id);

        return ResponseEntity.ok(
                usuarioService.updateUsuario(id, usuario));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(
            @PathVariable int id) {

        log.info("DELETE /api/v1/usuarios/{}", id);

        usuarioService.deleteUsuario(id);

        return ResponseEntity.noContent().build();
    }
}