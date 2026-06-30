package com.example.booksyspa.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.booksyspa.controller.LibroController;
import com.example.booksyspa.model.Categoria;

@Component
public class CategoriaAssembler implements RepresentationModelAssembler<Categoria, EntityModel<Categoria>> {

    @Override
    public EntityModel<Categoria> toModel(Categoria categoria) {
        Link librosLink = linkTo(methodOn(LibroController.class).getLibrosByCategoria(categoria.getIdCategoria()))
                .withRel("libros-de-la-categoria");

        return EntityModel.of(categoria, librosLink);
    }
}
