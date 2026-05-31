package com.example.msusuarios.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.msusuarios.exception.ResourceNotFoundException;
import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.repository.UsuarioRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getUsuarios() {
        log.info("Consultando todos los usuarios");
        return usuarioRepository.findAll();
    }

    public Usuario getUsuarioById(int id) {
        log.info("Buscando usuario con id={}", id);
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no existe"));
    }

    public Usuario getUsuarioByEmail(String email) {
        log.info("Buscando usuario con email={}", email);
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con email " + email + " no existe"));
    }

    public Usuario saveUsuario(Usuario usuario) {
        log.info("Creando usuario con email={}", usuario.getEmail());
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        Usuario nuevo = usuarioRepository.save(usuario);
        log.info("Usuario creado con id={}", nuevo.getIdUsuario());
        return nuevo;
    }

    @Transactional
    public Usuario updateUsuario(int id, Usuario datos) {
        log.info("Actualizando usuario con id={}", id);
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no existe"));

        Optional.ofNullable(datos.getNombre()).ifPresent(existente::setNombre);
        Optional.ofNullable(datos.getEmail()).ifPresent(existente::setEmail);
        Optional.ofNullable(datos.getPassword()).ifPresent(existente::setPassword);
        Optional.ofNullable(datos.getRol()).ifPresent(existente::setRol);

        Usuario actualizado = usuarioRepository.save(existente);
        log.info("Usuario actualizado con id={}", actualizado.getIdUsuario());
        return actualizado;
    }

    @Transactional
    public void deleteUsuario(int id) {
        log.info("Eliminando usuario con id={}", id);
        usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no existe"));
        usuarioRepository.deleteById(id);
    }
}