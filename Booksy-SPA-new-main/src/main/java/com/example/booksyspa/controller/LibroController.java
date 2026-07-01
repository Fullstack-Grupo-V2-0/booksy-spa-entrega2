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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Tag(name = "Libros", description = "Gestión del catálogo de libros")
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

    @Operation(summary = "Obtener todos los libros", description = "Retorna la lista completa de libros disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente")
    @GetMapping
    public CollectionModel<EntityModel<Libro>> getAllLibros() {
        log.info("GET /api/v2/libros");
        List<EntityModel<Libro>> libros = libroService.getLibros().stream()
                .map(libroAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(libros);
    }

    @Operation(summary = "Obtener libro por ID", description = "Retorna un libro específico según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Libro encontrado"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    @GetMapping("/{id}")
    public EntityModel<Libro> getLibroById(
            @Parameter(description = "ID del libro a buscar", required = true, example = "1")
            @PathVariable int id) {
        log.info("GET /api/v2/libros/{}", id);
        return libroAssembler.toModel(libroService.getLibroId(id));
    }

    @Operation(summary = "Obtener libro por ISBN", description = "Retorna un libro específico según su ISBN")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Libro encontrado"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    @GetMapping("/isbn/{isbn}")
    public EntityModel<Libro> getLibroByIsbn(
            @Parameter(description = "ISBN del libro a buscar", required = true, example = "978-8433966636")
            @PathVariable String isbn) {
        log.info("GET /api/v2/libros/isbn/{}", isbn);
        return libroAssembler.toModel(libroService.getLibroPorIsbn(isbn));
    }

    @Operation(summary = "Obtener libros por categoría", description = "Retorna todos los libros que pertenecen a una categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Categoría no encontrada")
    })
    @GetMapping("/categoria/{idCategoria}")
    public CollectionModel<EntityModel<Libro>> getLibrosByCategoria(
            @Parameter(description = "ID de la categoría", required = true, example = "1")
            @PathVariable int idCategoria) {
        log.info("GET /api/v2/libros/categoria/{}", idCategoria);
        List<EntityModel<Libro>> libros = libroService.getLibrosPorCategoria(idCategoria).stream()
                .map(libroAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(libros);
    }

    @Operation(summary = "Obtener libros por autor", description = "Retorna todos los libros de un autor específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de libros obtenida exitosamente"),
        @ApiResponse(responseCode = "404", description = "Autor no encontrado")
    })
    @GetMapping("/autor/{idAutor}")
    public CollectionModel<EntityModel<Libro>> getLibrosByAutor(
            @Parameter(description = "ID del autor", required = true, example = "1")
            @PathVariable int idAutor) {
        log.info("GET /api/v2/libros/autor/{}", idAutor);
        List<EntityModel<Libro>> libros = libroService.getLibrosPorAutor(idAutor).stream()
                .map(libroAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(libros);
    }

    @Operation(summary = "Crear nuevo libro", description = "Crea un libro y lo asocia a un autor y categoría existentes")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Libro creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o faltantes")
    })
    @PostMapping
    public ResponseEntity<EntityModel<Libro>> createLibro(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos del libro a crear", required = true)
            @Valid @RequestBody Libro libro) {
        log.info("POST /api/v2/libros");
        return new ResponseEntity<>(libroAssembler.toModel(libroService.saveLibro(libro)), HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar libro", description = "Actualiza los datos de un libro existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Libro actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Libro>> updateLibro(
            @Parameter(description = "ID del libro a actualizar", required = true, example = "1")
            @PathVariable int id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Datos actualizados del libro", required = true)
            @Valid @RequestBody Libro libro) {
        log.info("PUT /api/v2/libros/{}", id);
        return ResponseEntity.ok(libroAssembler.toModel(libroService.updateLibro(id, libro)));
    }

    @Operation(summary = "Eliminar libro", description = "Elimina un libro según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Libro eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Libro no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLibro(
            @Parameter(description = "ID del libro a eliminar", required = true, example = "1")
            @PathVariable int id) {
        log.info("DELETE /api/v2/libros/{}", id);
        libroService.deleteLibro(id);
        return ResponseEntity.noContent().build();
    }
}