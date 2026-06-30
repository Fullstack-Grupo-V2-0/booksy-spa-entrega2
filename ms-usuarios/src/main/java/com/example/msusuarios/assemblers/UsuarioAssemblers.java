package com.example.msusuarios.assemblers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import com.example.msusuarios.controller.UsuarioController;
import com.example.msusuarios.model.Usuario;

@Component
public class UsuarioAssemblers implements RepresentationModelAssembler<Usuario, EntityModel<Usuario>> {

    @Override
    public EntityModel<Usuario> toModel(Usuario usuario) {
        Link editarLink = linkTo(methodOn(UsuarioController.class).updateUsuario(usuario.getIdUsuario(), usuario))
                .withRel("editar");

        Link eliminarLink = linkTo(methodOn(UsuarioController.class).deleteUsuario(usuario.getIdUsuario()))
                .withRel("eliminar");

        return EntityModel.of(usuario, editarLink, eliminarLink);
    }
}
