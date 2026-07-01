package com.example.booksyspa.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Autor;
import com.example.booksyspa.model.Categoria;
import com.example.booksyspa.model.Libro;
import com.example.booksyspa.repository.AutorRepository;
import com.example.booksyspa.repository.CategoriaRepository;
import com.example.booksyspa.repository.LibroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class LibroServiceTest {

    @Autowired
    private LibroService libroService;

    @MockitoBean
    private LibroRepository libroRepository;

    @MockitoBean
    private AutorRepository autorRepository;

    @MockitoBean
    private CategoriaRepository categoriaRepository;

    private Autor autor;
    private Categoria categoria;
    private Libro libro;

    @BeforeEach
    void setUp() {
        // Configura las entidades de prueba antes de cada test
        autor = new Autor();
        autor.setIdAutor(1);
        autor.setNombre("Gabriel");
        autor.setApellido("García Márquez");

        categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Ficción");

        libro = new Libro();
        libro.setIdLibro(1);
        libro.setIsbn("978-0-06-112008-4");
        libro.setTitulo("Cien años de soledad");
        libro.setAutor(autor);
        libro.setCategoria(categoria);
        libro.setDescripcion("Obra maestra del realismo mágico");
        libro.setFechaPublicacion(LocalDate.of(1967, 5, 30));
        libro.setPrecio(new BigDecimal("15000"));
    }

    @Test
    public void testGetLibros() {
        // Define que el repositorio retorna una lista con un libro
        when(libroRepository.findAll()).thenReturn(List.of(libro));

        List<Libro> result = libroService.getLibros();

        // Verifica que la lista no sea nula y tenga exactamente un libro
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cien años de soledad", result.get(0).getTitulo());
    }

    @Test
    public void testGetLibroId_existe() {
        // Define que el libro con id 1 existe
        when(libroRepository.findById(1)).thenReturn(Optional.of(libro));

        Libro found = libroService.getLibroId(1);

        // Verifica que el libro devuelto sea el correcto
        assertNotNull(found);
        assertEquals(1, found.getIdLibro());
        assertEquals("978-0-06-112008-4", found.getIsbn());
    }

    @Test
    public void testGetLibroId_noExiste() {
        // Simula que el libro con id 99 no existe
        when(libroRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> libroService.getLibroId(99));
    }

    @Test
    public void testGetLibroPorIsbn_existe() {
        // Define que el libro con el ISBN dado existe
        when(libroRepository.findByIsbn("978-0-06-112008-4")).thenReturn(Optional.of(libro));

        Libro found = libroService.getLibroPorIsbn("978-0-06-112008-4");

        // Verifica que el libro devuelto sea el correcto
        assertNotNull(found);
        assertEquals("Cien años de soledad", found.getTitulo());
    }

    @Test
    public void testSaveLibro() {
        // Simula que el autor, categoría existen y el ISBN no está duplicado
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(libroRepository.findByIsbn("978-0-06-112008-4")).thenReturn(Optional.empty());
        when(libroRepository.save(any(Libro.class))).thenReturn(libro);

        Libro saved = libroService.saveLibro(libro);

        // Verifica que el libro guardado sea el correcto
        assertNotNull(saved);
        assertEquals("Cien años de soledad", saved.getTitulo());
        verify(libroRepository, times(1)).save(any(Libro.class));
    }

    @Test
    public void testSaveLibro_isbnDuplicado() {
        // Simula autor y categoría válidos pero ISBN ya registrado
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(libroRepository.findByIsbn("978-0-06-112008-4")).thenReturn(Optional.of(libro));

        // Verifica que se lanza RuntimeException por ISBN duplicado
        assertThrows(RuntimeException.class, () -> libroService.saveLibro(libro));
        verify(libroRepository, never()).save(any());
    }

    @Test
    public void testGetLibrosPorCategoria() {
        // Define que el repositorio retorna libros de la categoría id 1
        when(libroRepository.findByCategoriaIdCategoria(1)).thenReturn(List.of(libro));

        List<Libro> result = libroService.getLibrosPorCategoria(1);

        // Verifica que la lista tenga exactamente un libro
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetLibrosPorAutor() {
        // Define que el repositorio retorna libros del autor id 1
        when(libroRepository.findByAutorIdAutor(1)).thenReturn(List.of(libro));

        List<Libro> result = libroService.getLibrosPorAutor(1);

        // Verifica que la lista tenga exactamente un libro
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Cien años de soledad", result.get(0).getTitulo());
    }

    @Test
    public void testDeleteLibro() {
        // El libro existe y se elimina correctamente
        when(libroRepository.findById(1)).thenReturn(Optional.of(libro));
        doNothing().when(libroRepository).deleteById(1);

        libroService.deleteLibro(1);

        // Verifica que deleteById se haya llamado una vez
        verify(libroRepository, times(1)).deleteById(1);
    }
}
