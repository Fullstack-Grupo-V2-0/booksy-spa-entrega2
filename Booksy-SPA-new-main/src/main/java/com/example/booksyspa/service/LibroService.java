package com.example.booksyspa.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Autor;
import com.example.booksyspa.model.Categoria;
import com.example.booksyspa.model.Libro;
import com.example.booksyspa.repository.AutorRepository;
import com.example.booksyspa.repository.CategoriaRepository;
import com.example.booksyspa.repository.LibroRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private AutorRepository autorRepository;

    public List<Libro> getLibros() {
        log.info("Consultando todos los libros");
        return libroRepository.findAll();
    }

    @Transactional
    public Libro saveLibro(Libro libro) {

        log.info("Intentando guardar libro con ISBN={}", libro.getIsbn());

        // Validar autor
        Autor autor = autorRepository.findById(libro.getAutor().getIdAutor())
                .orElseThrow(() -> {
                    log.warn("No se encontró autor con id={}", libro.getAutor().getIdAutor());
                    return new ResourceNotFoundException(
                            "El autor con id " + libro.getAutor().getIdAutor() + " no existe");
                });

        // Validar categoría
        Categoria categoria = categoriaRepository.findById(libro.getCategoria().getIdCategoria())
                .orElseThrow(() -> {
                    log.warn("No se encontró categoría con id={}", libro.getCategoria().getIdCategoria());
                    return new ResourceNotFoundException(
                            "La categoría con id " + libro.getCategoria().getIdCategoria() + " no existe");
                });

        // Validar ISBN único
        libroRepository.findByIsbn(libro.getIsbn()).ifPresent(l -> {
            log.warn("Intento de registrar libro con ISBN duplicado={}", libro.getIsbn());
            throw new RuntimeException("El ISBN " + libro.getIsbn() + " ya está registrado.");
        });

        libro.setAutor(autor);
        libro.setCategoria(categoria);

        Libro libroGuardado = libroRepository.save(libro);

        log.info("Libro guardado correctamente con id={}", libroGuardado.getIdLibro());

        return libroGuardado;
    }

    public Libro getLibroId(int id) {

        log.info("Buscando libro con id={}", id);

        return libroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró libro con id={}", id);
                    return new ResourceNotFoundException("El libro con id " + id + " no existe");
                });
    }

    public Libro getLibroPorIsbn(String isbn) {

        log.info("Buscando libro con ISBN={}", isbn);

        return libroRepository.findByIsbn(isbn)
                .orElseThrow(() -> {
                    log.warn("No se encontró libro con ISBN={}", isbn);
                    return new ResourceNotFoundException("El libro con ISBN " + isbn + " no existe");
                });
    }

    public List<Libro> getLibrosPorCategoria(int idCategoria) {

        log.info("Consultando libros de categoría id={}", idCategoria);

        return libroRepository.findByCategoriaIdCategoria(idCategoria);
    }

    public List<Libro> getLibrosPorAutor(int idAutor) {

        log.info("Consultando libros de autor id={}", idAutor);

        return libroRepository.findByAutorIdAutor(idAutor);
    }

    @Transactional
    public Libro updateLibro(int id, Libro libroActualizado) {

        log.info("Actualizando libro con id={}", id);

        Libro libroExistente = libroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró libro con id={} para actualizar", id);
                    return new ResourceNotFoundException("El libro con id " + id + " no existe");
                });

        log.debug("Libro encontrado para actualización: {}", libroExistente);

        // Validar ISBN
        if (libroActualizado.getIsbn() != null
                && !libroActualizado.getIsbn().equals(libroExistente.getIsbn())) {

            log.info("Validando cambio de ISBN para libro id={}", id);

            libroRepository.findByIsbn(libroActualizado.getIsbn()).ifPresent(l -> {
                log.warn("Intento de actualizar libro id={} con ISBN duplicado={}",
                        id, libroActualizado.getIsbn());

                throw new RuntimeException(
                        "El ISBN " + libroActualizado.getIsbn() + " ya está en uso por otro libro.");
            });

            libroExistente.setIsbn(libroActualizado.getIsbn());
        }

        // Validar autor
        if (libroActualizado.getAutor() != null
                && libroActualizado.getAutor().getIdAutor() != 0) {

            Autor nuevoAutor = autorRepository.findById(libroActualizado.getAutor().getIdAutor())
                    .orElseThrow(() -> {
                        log.warn("No se encontró autor con id={}",
                                libroActualizado.getAutor().getIdAutor());

                        return new ResourceNotFoundException(
                                "El autor con id "
                                        + libroActualizado.getAutor().getIdAutor()
                                        + " no existe");
                    });

            libroExistente.setAutor(nuevoAutor);
        }

        // Validar categoría
        if (libroActualizado.getCategoria() != null
                && libroActualizado.getCategoria().getIdCategoria() != 0) {

            Categoria nuevaCategoria = categoriaRepository.findById(
                    libroActualizado.getCategoria().getIdCategoria())
                    .orElseThrow(() -> {
                        log.warn("No se encontró categoría con id={}",
                                libroActualizado.getCategoria().getIdCategoria());

                        return new ResourceNotFoundException(
                                "La categoría con id "
                                        + libroActualizado.getCategoria().getIdCategoria()
                                        + " no existe");
                    });

            libroExistente.setCategoria(nuevaCategoria);
        }

        // Actualización selectiva
        Optional.ofNullable(libroActualizado.getTitulo()).ifPresent(libroExistente::setTitulo);
        Optional.ofNullable(libroActualizado.getDescripcion()).ifPresent(libroExistente::setDescripcion);
        Optional.ofNullable(libroActualizado.getFechaPublicacion()).ifPresent(libroExistente::setFechaPublicacion);
        Optional.ofNullable(libroActualizado.getPrecio()).ifPresent(libroExistente::setPrecio);

        log.info("Persistiendo cambios del libro id={}", id);

        Libro libroGuardado = libroRepository.save(libroExistente);

        log.info("Libro id={} actualizado correctamente", id);

        return libroGuardado;
    }

    @Transactional
    public void deleteLibro(int id) {

        log.info("Eliminando libro con id={}", id);

        libroRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("No se encontró libro con id={}", id);
                    return new ResourceNotFoundException("El libro con id " + id + " no existe");
                });

        log.debug("Libro id={} encontrado, procediendo a eliminar", id);

        libroRepository.deleteById(id);

        log.info("Libro id={} eliminado correctamente", id);
    }
}