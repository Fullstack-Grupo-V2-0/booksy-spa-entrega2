package com.example.booksyspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Categoria;
import com.example.booksyspa.repository.CategoriaRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Categoria> getCategorias() {
        log.info("Consultando todas las categorías");
        return categoriaRepository.findAll();
    }

    public Categoria saveCategoria(Categoria categoria) {
        log.info("Guardando categoría nombre={}", categoria.getNombre());

        Categoria categoriaGuardada = categoriaRepository.save(categoria);

        log.info("Categoría guardada correctamente con id={}",
                categoriaGuardada.getIdCategoria());

        return categoriaGuardada;
    }

    public Categoria getCategoriaId(int id) {
        log.info("Buscando categoría con id={}", id);

        return categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró categoría con id={}", id);
                    return new ResourceNotFoundException("La categoría con id " + id + " no existe");
                });
    }

    @Transactional
    public Categoria updateCategoria(int id, Categoria categoriaActualizada) {
        log.info("Actualizando categoría con id={}", id);

        Categoria categoriaExistente = categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró categoría con id={} para actualizar", id);
                    return new ResourceNotFoundException("La categoría con id " + id + " no existe");
                });

        log.debug("Categoría encontrada para actualización: {}", categoriaExistente);

        Optional.ofNullable(categoriaActualizada.getNombre())
                .ifPresent(categoriaExistente::setNombre);

        Optional.ofNullable(categoriaActualizada.getDescripcion())
                .ifPresent(categoriaExistente::setDescripcion);

        log.info("Persistiendo cambios de la categoría id={}", id);

        Categoria categoriaGuardada = categoriaRepository.save(categoriaExistente);

        log.info("Categoría id={} actualizada correctamente", id);

        return categoriaGuardada;
    }

    @Transactional
    public void deleteCategoria(int id) {
        log.info("Eliminando categoría con id={}", id);

        categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró categoría con id={}", id);
                    return new ResourceNotFoundException("La categoría con id " + id + " no existe");
                });

        log.debug("Categoría id={} encontrada, procediendo a eliminar", id);

        categoriaRepository.deleteById(id);

        log.info("Categoría id={} eliminada correctamente", id);
    }
}