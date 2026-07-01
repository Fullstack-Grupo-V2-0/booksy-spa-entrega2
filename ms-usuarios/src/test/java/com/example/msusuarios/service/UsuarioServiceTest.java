package com.example.msusuarios.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.msusuarios.exception.ResourceNotFoundException;
import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Admin Test");
        usuario.setEmail("admin@test.cl");
        usuario.setPassword("1234");
        usuario.setRol("ADMIN");
        usuario.setActivo(1);
    }

    @Test
    public void testGetUsuarios() {
        // Define el comportamiento del mock: retorna lista con un usuario
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<Usuario> result = usuarioService.getUsuarios();

        // Verifica que la lista no sea nula y contenga exactamente un usuario
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Admin Test", result.get(0).getNombre());
    }

    @Test
    public void testGetUsuarioById_existe() {
        // Define que el usuario con id 1 existe
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));

        Usuario found = usuarioService.getUsuarioById(1);

        // Verifica que el usuario devuelto sea el correcto
        assertNotNull(found);
        assertEquals(1, found.getIdUsuario());
        assertEquals("admin@test.cl", found.getEmail());
    }

    @Test
    public void testGetUsuarioById_noExiste() {
        // Simula que el usuario con id 99 no existe
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lance ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.getUsuarioById(99));
    }

    @Test
    public void testGetUsuarioByEmail_existe() {
        // Define que el usuario con el email dado existe
        when(usuarioRepository.findByEmail("admin@test.cl")).thenReturn(Optional.of(usuario));

        Usuario found = usuarioService.getUsuarioByEmail("admin@test.cl");

        // Verifica que el usuario devuelto sea correcto
        assertNotNull(found);
        assertEquals("Admin Test", found.getNombre());
    }

    @Test
    public void testGetUsuarioByEmail_noExiste() {
        // Simula que el email no está registrado
        when(usuarioRepository.findByEmail("noexiste@test.cl")).thenReturn(Optional.empty());

        // Verifica que se lance ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class,
                () -> usuarioService.getUsuarioByEmail("noexiste@test.cl"));
    }

    @Test
    public void testSaveUsuario() {
        // Simula que el email no existe y que save retorna el usuario guardado
        when(usuarioRepository.existsByEmail("admin@test.cl")).thenReturn(false);
        when(usuarioRepository.save(usuario)).thenReturn(usuario);

        Usuario saved = usuarioService.saveUsuario(usuario);

        // Verifica que el usuario guardado sea el correcto
        assertNotNull(saved);
        assertEquals("admin@test.cl", saved.getEmail());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    public void testSaveUsuario_emailDuplicado() {
        // Simula que el email ya existe en el sistema
        when(usuarioRepository.existsByEmail("admin@test.cl")).thenReturn(true);

        // Verifica que se lance RuntimeException por email duplicado
        assertThrows(RuntimeException.class, () -> usuarioService.saveUsuario(usuario));
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    public void testUpdateUsuario() {
        // El usuario existe y se actualiza con nuevos datos
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario datos = new Usuario();
        datos.setNombre("Nombre Actualizado");

        Usuario updated = usuarioService.updateUsuario(1, datos);

        // Verifica que el update se persistió
        assertNotNull(updated);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    public void testDeleteUsuario() {
        // El usuario existe y se elimina correctamente
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        doNothing().when(usuarioRepository).deleteById(1);

        usuarioService.deleteUsuario(1);

        // Verifica que deleteById se haya llamado una vez
        verify(usuarioRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteUsuario_noExiste() {
        // Simula que el usuario a eliminar no existe
        when(usuarioRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lance ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> usuarioService.deleteUsuario(99));
        verify(usuarioRepository, never()).deleteById(any());
    }
}
