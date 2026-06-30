package com.example.booksyspa.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.booksyspa.controller.AutorController;
import com.example.booksyspa.controller.LibroController;
import com.example.booksyspa.model.Autor;

@Component
public class AutorAssembler implements RepresentationModelAssembler<Autor, EntityModel<Autor>> {

    @Override
    public EntityModel<Autor> toModel(Autor autor) {
        Link editarLink = linkTo(methodOn(AutorController.class).updateAutor(autor.getIdAutor(), autor))
                .withRel("editar");

        Link eliminarLink = linkTo(methodOn(AutorController.class).deleteAutor(autor.getIdAutor()))
                .withRel("eliminar");

        Link librosLink = linkTo(methodOn(LibroController.class).getLibrosByAutor(autor.getIdAutor()))
                .withRel("libros-del-autor");

        return EntityModel.of(autor, editarLink, eliminarLink, librosLink);
    }
}
