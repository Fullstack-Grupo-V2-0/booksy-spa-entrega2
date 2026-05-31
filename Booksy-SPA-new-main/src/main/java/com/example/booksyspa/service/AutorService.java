package com.example.booksyspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Autor;
import com.example.booksyspa.repository.AutorRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AutorService {

    @Autowired
    private AutorRepository autorRepository;

    public List<Autor> obtenerAutores() {
        log.info("Consultando todos los autores");
        return autorRepository.findAll();
    }

    public Autor buscarAutorPorId(int id) {

        log.info("Buscando autor con id={}", id);

        return autorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró autor con id={}", id);
                    return new ResourceNotFoundException("El autor con id " + id + " no existe");
                });
    }

    public Autor buscarAutorPorNombre(String nombre) {

        log.info("Buscando autor con nombre={}", nombre);

        Autor autor = autorRepository.findByNombre(nombre).orElse(null);

        if (autor == null) {
            log.warn("No se encontró autor con nombre={}", nombre);
        }

        return autor;
    }

    public Autor guardar(Autor autor) {

        log.info("Guardando autor nombre={} apellido={}",
                autor.getNombre(),
                autor.getApellido());

        Autor autorGuardado = autorRepository.save(autor);

        log.info("Autor guardado correctamente con id={}",
                autorGuardado.getIdAutor());

        return autorGuardado;
    }

    @Transactional
    public Autor actualizar(int id, Autor autorActualizado) {

        log.info("Actualizando autor con id={}", id);

        Autor autorExistente = autorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró autor con id={} para actualizar", id);
                    return new ResourceNotFoundException("El autor con id " + id + " no existe");
                });

        log.debug("Autor encontrado para actualización: {}", autorExistente);

        Optional.ofNullable(autorActualizado.getNombre())
                .ifPresent(autorExistente::setNombre);

        Optional.ofNullable(autorActualizado.getApellido())
                .ifPresent(autorExistente::setApellido);

        Optional.ofNullable(autorActualizado.getFechaNacimiento())
                .ifPresent(autorExistente::setFechaNacimiento);

        Optional.ofNullable(autorActualizado.getNacionalidad())
                .ifPresent(autorExistente::setNacionalidad);

        Optional.ofNullable(autorActualizado.getPremios())
                .ifPresent(autorExistente::setPremios);

        log.info("Persistiendo cambios del autor id={}", id);

        Autor autorGuardado = autorRepository.save(autorExistente);

        log.info("Autor id={} actualizado correctamente", id);

        return autorGuardado;
    }

    @Transactional
    public void eliminar(int id) {

        log.info("Eliminando autor con id={}", id);

        autorRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró autor con id={}", id);
                    return new ResourceNotFoundException("El autor con id " + id + " no existe");
                });

        log.debug("Autor id={} encontrado, procediendo a eliminar", id);

        autorRepository.deleteById(id);

        log.info("Autor id={} eliminado correctamente", id);
    }
}