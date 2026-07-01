package com.example.booksyspa.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Autor;
import com.example.booksyspa.repository.AutorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class AutorServiceTest {

    @Autowired
    private AutorService autorService;

    @MockitoBean
    private AutorRepository autorRepository;

    private Autor autor;

    @BeforeEach
    void setUp() {
        // Configura un autor de prueba antes de cada test
        autor = new Autor();
        autor.setIdAutor(1);
        autor.setNombre("Gabriel");
        autor.setApellido("García Márquez");
        autor.setFechaNacimiento(LocalDate.of(1927, 3, 6));
        autor.setNacionalidad("Colombiana");
        autor.setPremios("Premio Nobel de Literatura");
    }

    @Test
    public void testObtenerAutores() {
        // Define que el repositorio retorna una lista con un autor
        when(autorRepository.findAll()).thenReturn(List.of(autor));

        List<Autor> result = autorService.obtenerAutores();

        // Verifica que la lista no sea nula y tenga exactamente un autor
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gabriel", result.get(0).getNombre());
    }

    @Test
    public void testBuscarAutorPorId_existe() {
        // Define que el autor con id 1 existe
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));

        Autor found = autorService.buscarAutorPorId(1);

        // Verifica que el autor devuelto sea el correcto
        assertNotNull(found);
        assertEquals(1, found.getIdAutor());
        assertEquals("García Márquez", found.getApellido());
    }

    @Test
    public void testBuscarAutorPorId_noExiste() {
        // Simula que el autor con id 99 no existe
        when(autorRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> autorService.buscarAutorPorId(99));
    }

    @Test
    public void testBuscarAutorPorNombre_existe() {
        // Define que el autor con el nombre dado existe
        when(autorRepository.findByNombre("Gabriel")).thenReturn(Optional.of(autor));

        Autor found = autorService.buscarAutorPorNombre("Gabriel");

        // Verifica que el autor devuelto sea el correcto
        assertNotNull(found);
        assertEquals("Gabriel", found.getNombre());
    }

    @Test
    public void testBuscarAutorPorNombre_noExiste() {
        // Simula que el nombre no corresponde a ningún autor
        when(autorRepository.findByNombre("Desconocido")).thenReturn(Optional.empty());

        // El servicio retorna null cuando no encuentra el autor
        Autor found = autorService.buscarAutorPorNombre("Desconocido");

        assertNull(found);
    }

    @Test
    public void testGuardar() {
        // Define que save retorna el autor guardado
        when(autorRepository.save(autor)).thenReturn(autor);

        Autor saved = autorService.guardar(autor);

        // Verifica que el autor guardado sea el correcto
        assertNotNull(saved);
        assertEquals("García Márquez", saved.getApellido());
        verify(autorRepository, times(1)).save(autor);
    }

    @Test
    public void testActualizar() {
        // El autor existe y se actualiza con nuevos datos
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        when(autorRepository.save(any(Autor.class))).thenReturn(autor);

        Autor datos = new Autor();
        datos.setNacionalidad("Argentina");

        Autor updated = autorService.actualizar(1, datos);

        // Verifica que la actualización se persistió
        assertNotNull(updated);
        verify(autorRepository, times(1)).save(any(Autor.class));
    }

    @Test
    public void testEliminar() {
        // El autor existe y se elimina correctamente
        when(autorRepository.findById(1)).thenReturn(Optional.of(autor));
        doNothing().when(autorRepository).deleteById(1);

        autorService.eliminar(1);

        // Verifica que deleteById se haya llamado una vez
        verify(autorRepository, times(1)).deleteById(1);
    }

    @Test
    public void testEliminar_noExiste() {
        // Simula que el autor a eliminar no existe
        when(autorRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> autorService.eliminar(99));
        verify(autorRepository, never()).deleteById(any());
    }
}
