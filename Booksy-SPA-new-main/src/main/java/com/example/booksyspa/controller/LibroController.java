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

import com.example.booksyspa.assemblers.LibroAssembler;
import com.example.booksyspa.model.Libro;
import com.example.booksyspa.service.LibroService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/libros")
@CrossOrigin("*")
public class LibroController {

    private final LibroService libroService;
    private final LibroAssembler libroAssembler;

    public LibroController(LibroService libroService, LibroAssembler libroAssembler) {
        this.libroService = libroService;
        this.libroAssembler = libroAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Libro>> getAllLibros() {
        log.info("GET /api/v2/libros");
        List<EntityModel<Libro>> libros = libroService.getLibros().stream()
                .map(libroAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(libros);
    }

    @GetMapping("/{id}")
    public EntityModel<Libro> getLibroById(@PathVariable int id) {
        log.info("GET /api/v2/libros/{}", id);
        return libroAssembler.toModel(libroService.getLibroId(id));
    }

    @GetMapping("/isbn/{isbn}")
    public EntityModel<Libro> getLibroByIsbn(@PathVariable String isbn) {
        log.info("GET /api/v2/libros/isbn/{}", isbn);
        return libroAssembler.toModel(libroService.getLibroPorIsbn(isbn));
    }

    @GetMapping(value = "/categoria/{idCategoria}")
    public CollectionModel<EntityModel<Libro>> getLibrosByCategoria(@PathVariable int idCategoria) {
        log.info("GET /api/v2/libros/categoria/{}", idCategoria);
        List<EntityModel<Libro>> libros = libroService.getLibrosPorCategoria(idCategoria).stream()
                .map(libroAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(libros);
    }

    @GetMapping(value = "/autor/{idAutor}")
    public CollectionModel<EntityModel<Libro>> getLibrosByAutor(@PathVariable int idAutor) {
        log.info("GET /api/v2/libros/autor/{}", idAutor);
        List<EntityModel<Libro>> libros = libroService.getLibrosPorAutor(idAutor).stream()
                .map(libroAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(libros);
    }

    @PostMapping
    public ResponseEntity<EntityModel<Libro>> createLibro(@Valid @RequestBody Libro libro) {
        log.info("POST /api/v2/libros");
        return new ResponseEntity<>(libroAssembler.toModel(libroService.saveLibro(libro)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Libro>> updateLibro(@PathVariable int id, @Valid @RequestBody Libro libro) {
        log.info("PUT /api/v2/libros/{}", id);
        return ResponseEntity.ok(libroAssembler.toModel(libroService.updateLibro(id, libro)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(@PathVariable int id) {
        log.info("DELETE /api/v2/libros/{}", id);
        libroService.deleteLibro(id);
        return ResponseEntity.noContent().build();
    }
}
