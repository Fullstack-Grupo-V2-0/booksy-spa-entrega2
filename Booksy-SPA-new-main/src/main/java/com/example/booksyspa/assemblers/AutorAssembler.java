package com.example.booksyspa.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.example.booksyspa.controller.AutorController;
import com.example.booksyspa.controller.LibroController;
import com.example.booksyspa.model.Autor;

@Component
public class AutorAssembler implements RepresentationModelAssembler<Autor, EntityModel<Autor>> {

    @Override
    public EntityModel<Autor> toModel(Autor autor) {
        Link selfLink = linkTo(methodOn(AutorController.class).getAutorById(autor.getIdAutor())).withSelfRel();
        Link librosLink = linkTo(methodOn(LibroController.class).getLibrosByAutor(autor.getIdAutor())).withRel("libros-del-autor");
        return EntityModel.of(autor, selfLink, librosLink);
    }
}
