package com.example.booksyspa.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.booksyspa.controller.PedidoController;
import com.example.booksyspa.model.Cliente;

@Component
public class ClienteAssembler implements RepresentationModelAssembler<Cliente, EntityModel<Cliente>> {

    @Override
    public EntityModel<Cliente> toModel(Cliente cliente) {
        Link pedidosLink = linkTo(methodOn(PedidoController.class).getPedidosByCliente(cliente.getIdCliente()))
                .withRel("pedidos-del-cliente");

        return EntityModel.of(cliente, pedidosLink);
    }
}
