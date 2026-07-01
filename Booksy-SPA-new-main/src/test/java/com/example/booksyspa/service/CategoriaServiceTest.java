package com.example.booksyspa.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Categoria;
import com.example.booksyspa.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class CategoriaServiceTest {

    @Autowired
    private CategoriaService categoriaService;

    @MockitoBean
    private CategoriaRepository categoriaRepository;

    private Categoria categoria;

    @BeforeEach
    void setUp() {
        // Configura una categoría de prueba antes de cada test
        categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Ficción");
        categoria.setDescripcion("Novelas de ficción literaria");
    }

    @Test
    public void testGetCategorias() {
        // Define que el repositorio retorna una lista con una categoría
        when(categoriaRepository.findAll()).thenReturn(List.of(categoria));

        List<Categoria> result = categoriaService.getCategorias();

        // Verifica que la lista no sea nula y tenga exactamente una categoría
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ficción", result.get(0).getNombre());
    }

    @Test
    public void testGetCategoriaId_existe() {
        // Define que la categoría con id 1 existe
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));

        Categoria found = categoriaService.getCategoriaId(1);

        // Verifica que la categoría devuelta sea la correcta
        assertNotNull(found);
        assertEquals(1, found.getIdCategoria());
        assertEquals("Ficción", found.getNombre());
    }

    @Test
    public void testGetCategoriaId_noExiste() {
        // Simula que la categoría con id 99 no existe
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.getCategoriaId(99));
    }

    @Test
    public void testSaveCategoria() {
        // Define que save retorna la categoría guardada
        when(categoriaRepository.save(categoria)).thenReturn(categoria);

        Categoria saved = categoriaService.saveCategoria(categoria);

        // Verifica que la categoría guardada sea la correcta
        assertNotNull(saved);
        assertEquals("Ficción", saved.getNombre());
        verify(categoriaRepository, times(1)).save(categoria);
    }

    @Test
    public void testUpdateCategoria() {
        // La categoría existe y se actualiza con nuevos datos
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoria);

        Categoria datos = new Categoria();
        datos.setNombre("Ciencia Ficción");
        datos.setDescripcion("Literatura de ciencia ficción");

        Categoria updated = categoriaService.updateCategoria(1, datos);

        // Verifica que la actualización se persistió
        assertNotNull(updated);
        verify(categoriaRepository, times(1)).save(any(Categoria.class));
    }

    @Test
    public void testDeleteCategoria() {
        // La categoría existe y se elimina correctamente
        when(categoriaRepository.findById(1)).thenReturn(Optional.of(categoria));
        doNothing().when(categoriaRepository).deleteById(1);

        categoriaService.deleteCategoria(1);

        // Verifica que deleteById se haya llamado una vez
        verify(categoriaRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteCategoria_noExiste() {
        // Simula que la categoría a eliminar no existe
        when(categoriaRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> categoriaService.deleteCategoria(99));
        verify(categoriaRepository, never()).deleteById(any());
    }
}
