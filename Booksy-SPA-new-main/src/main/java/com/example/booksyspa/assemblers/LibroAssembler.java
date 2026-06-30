package com.example.booksyspa.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.booksyspa.controller.AutorController;
import com.example.booksyspa.controller.CategoriaController;
import com.example.booksyspa.controller.LibroController;
import com.example.booksyspa.controller.PedidoController;
import com.example.booksyspa.model.Libro;

@Component
public class LibroAssembler implements RepresentationModelAssembler<Libro, EntityModel<Libro>> {

    @Override
    public EntityModel<Libro> toModel(Libro libro) {
        Link editarLink = linkTo(methodOn(LibroController.class).updateLibro(libro.getIdLibro(), libro))
                .withRel("editar");

        Link eliminarLink = linkTo(methodOn(LibroController.class).deleteLibro(libro.getIdLibro()))
                .withRel("eliminar");

        Link autorLink = linkTo(methodOn(AutorController.class).getAutorById(libro.getAutor().getIdAutor()))
                .withRel("autor-del-libro");

        Link categoriaLink = linkTo(methodOn(CategoriaController.class).getCategoriaById(libro.getCategoria().getIdCategoria()))
                .withRel("categoria-del-libro");

        Link pedidosLink = linkTo(methodOn(PedidoController.class).getPedidosByLibro(libro.getIdLibro()))
                .withRel("pedidos-del-libro");

        return EntityModel.of(libro, editarLink, eliminarLink, autorLink, categoriaLink, pedidosLink);
    }
}
