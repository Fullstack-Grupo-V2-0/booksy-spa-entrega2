package com.example.booksyspa.controller;

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

import com.example.booksyspa.assemblers.CategoriaAssembler;
import com.example.booksyspa.model.Categoria;
import com.example.booksyspa.service.CategoriaService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v2/categorias")
@CrossOrigin("*")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private final CategoriaAssembler categoriaAssembler;

    public CategoriaController(CategoriaService categoriaService, CategoriaAssembler categoriaAssembler) {
        this.categoriaService = categoriaService;
        this.categoriaAssembler = categoriaAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Categoria>> getAllCategorias() {
        log.info("GET /api/v2/categorias");
        List<EntityModel<Categoria>> categorias = categoriaService.getCategorias().stream()
                .map(categoriaAssembler::toModel)
                .collect(Collectors.toList());
        return CollectionModel.of(categorias,
                linkTo(methodOn(CategoriaController.class).getAllCategorias()).withSelfRel());
    }

    @GetMapping("/{id}")
    public EntityModel<Categoria> getCategoriaById(@PathVariable int id) {
        log.info("GET /api/v2/categorias/{}", id);
        return categoriaAssembler.toModel(categoriaService.getCategoriaId(id));
    }

    @PostMapping
    public ResponseEntity<EntityModel<Categoria>> createCategoria(@Valid @RequestBody Categoria categoria) {
        log.info("POST /api/v2/categorias");
        return new ResponseEntity<>(categoriaAssembler.toModel(categoriaService.saveCategoria(categoria)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Categoria>> updateCategoria(@PathVariable int id, @Valid @RequestBody Categoria categoria) {
        log.info("PUT /api/v2/categorias/{}", id);
        return ResponseEntity.ok(categoriaAssembler.toModel(categoriaService.updateCategoria(id, categoria)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategoria(@PathVariable int id) {
        log.info("DELETE /api/v2/categorias/{}", id);
        categoriaService.deleteCategoria(id);
        return ResponseEntity.noContent().build();
    }
}
