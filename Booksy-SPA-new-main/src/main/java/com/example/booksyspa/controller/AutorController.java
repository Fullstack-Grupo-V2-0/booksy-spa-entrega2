package com.example.booksyspa.controller;

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

import com.example.booksyspa.assemblers.AutorAssembler;
import com.example.booksyspa.model.Autor;
import com.example.booksyspa.service.AutorService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.tags.Tag;


@Tag(name = "Autores", description = "Gestión de autores")
@Slf4j
@RestController
@RequestMapping("/api/v2/autores")
@CrossOrigin("*")
public class AutorController {

    private final AutorService autorService;
    private final AutorAssembler autorAssembler;

    public AutorController(AutorService autorService, AutorAssembler autorAssembler) {
        this.autorService = autorService;
        this.autorAssembler = autorAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Autor>> getAllAutores() {
        log.info("GET /api/v2/autores");
        List<EntityModel<Autor>> autores = autorService.obtenerAutores().stream()
                .map(autorAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(autores);
    }

    @GetMapping("/{id}")
    public EntityModel<Autor> getAutorById(@PathVariable int id) {
        log.info("GET /api/v2/autores/{}", id);
        return autorAssembler.toModel(autorService.buscarAutorPorId(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Autor>> createAutor(@Valid @RequestBody Autor autor) {
        log.info("POST /api/v2/autores");
        return new ResponseEntity<>(autorAssembler.toModel(autorService.guardar(autor)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Autor>> updateAutor(@PathVariable int id, @Valid @RequestBody Autor autor) {
        log.info("PUT /api/v2/autores/{}", id);
        return ResponseEntity.ok(autorAssembler.toModel(autorService.actualizar(id, autor)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAutor(@PathVariable int id) {
        log.info("DELETE /api/v2/autores/{}", id);
        autorService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
