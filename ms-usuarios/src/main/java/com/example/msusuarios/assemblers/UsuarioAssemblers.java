package com.example.msusuarios.assemblers;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.stereotype.Component;

import com.example.msusuarios.controller.UsuarioController;
import com.example.msusuarios.model.Usuario;

@Component
public class UsuarioAssemblers implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        Link selfLink = linkTo(methodOn(UsuarioController.class).getUsuarioById(usuario.getIdUsuario())).withSelfRel();
        return EntityModel.of(usuario, selfLink);
    }
}
