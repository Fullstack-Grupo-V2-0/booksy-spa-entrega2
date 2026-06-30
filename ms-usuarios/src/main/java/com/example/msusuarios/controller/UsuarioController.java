package com.example.msusuarios.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import com.example.msusuarios.assemblers.UsuarioAssemblers;
import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.service.UsuarioService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioAssemblers assemblers;

    public UsuarioController(UsuarioService usuarioService, UsuarioAssemblers assemblers) {
        this.usuarioService = usuarioService;
        this.assemblers = assemblers;
    }

    @GetMapping
    public CollectionModel<EntityModel<Usuario>> getAllUsuarios() {
        log.info("GET /api/v1/usuarios");
        List<EntityModel<Usuario>> usuarios = usuarioService.getUsuarios().stream()
                .map(assemblers::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(usuarios,
                linkTo(methodOn(UsuarioController.class).getAllUsuarios()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Usuario> getUsuarioById(@PathVariable int id) {
        log.info("GET /api/v1/usuarios/{}", id);
        return assemblers.toModel(usuarioService.getUsuarioById(id));
    }

    @GetMapping("/email/{email}")
    public EntityModel<Usuario> getUsuarioByEmail(@PathVariable String email) {
        log.info("GET /api/v1/usuarios/email/{}", email);
        return assemblers.toModel(usuarioService.getUsuarioByEmail(email));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Usuario>> createUsuario(@Valid @RequestBody Usuario usuario) {
        log.info("POST /api/v1/usuarios - email={}", usuario.getEmail());
        return new ResponseEntity<>(assemblers.toModel(usuarioService.saveUsuario(usuario)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Usuario>> updateUsuario(@PathVariable int id, @Valid @RequestBody Usuario usuario) {
        log.info("PUT /api/v1/usuarios/{}", id);
        return ResponseEntity.ok(assemblers.toModel(usuarioService.updateUsuario(id, usuario)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable int id) {
        log.info("DELETE /api/v1/usuarios/{}", id);
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
