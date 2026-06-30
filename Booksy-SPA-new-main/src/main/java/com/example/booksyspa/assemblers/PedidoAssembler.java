package com.example.booksyspa.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.booksyspa.controller.ClienteController;
import com.example.booksyspa.controller.LibroController;
import com.example.booksyspa.model.Pedido;

@Component
public class PedidoAssembler implements RepresentationModelAssembler<Pedido, EntityModel<Pedido>> {

    @Override
    public EntityModel<Pedido> toModel(Pedido pedido) {
        Link clienteLink = linkTo(methodOn(ClienteController.class).getClienteById(pedido.getCliente().getIdCliente()))
                .withRel("cliente-del-pedido");

        Link libroLink = linkTo(methodOn(LibroController.class).getLibroById(pedido.getLibro().getIdLibro()))
                .withRel("libro-del-pedido");

        return EntityModel.of(pedido, clienteLink, libroLink);
    }
}
