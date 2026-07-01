package com.example.booksyspa.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.Cliente;
import com.example.booksyspa.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class ClienteServiceTest {

    @Autowired
    private ClienteService clienteService;

    @MockitoBean
    private ClienteRepository clienteRepository;

    private Cliente cliente;

    @BeforeEach
    void setUp() {
        // Configura un cliente de prueba antes de cada test
        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setRut("12345678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setEmail("juan@test.cl");
        cliente.setTelefono("912345678");
    }

    @Test
    public void testGetClientes() {
        // Define que el repositorio retorna una lista con un cliente
        when(clienteRepository.findAll()).thenReturn(List.of(cliente));

        List<Cliente> result = clienteService.getClientes();

        // Verifica que la lista no sea nula y tenga exactamente un cliente
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getNombre());
    }

    @Test
    public void testGetClienteId_existe() {
        // Define que el cliente con id 1 existe
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));

        Cliente found = clienteService.getClienteId(1);

        // Verifica que el cliente devuelto sea el correcto
        assertNotNull(found);
        assertEquals(1, found.getIdCliente());
        assertEquals("12345678-9", found.getRut());
    }

    @Test
    public void testGetClienteId_noExiste() {
        // Simula que el cliente con id 99 no existe
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> clienteService.getClienteId(99));
    }

    @Test
    public void testGetClientePorRut_existe() {
        // Define que el cliente con el RUT dado existe
        when(clienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(cliente));

        Cliente found = clienteService.getClientePorRut("12345678-9");

        // Verifica que el cliente devuelto sea el correcto
        assertNotNull(found);
        assertEquals("Juan", found.getNombre());
    }

    @Test
    public void testGetClientePorRut_noExiste() {
        // Simula que el RUT no está registrado
        when(clienteRepository.findByRut("99999999-9")).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class,
                () -> clienteService.getClientePorRut("99999999-9"));
    }

    @Test
    public void testSaveCliente() {
        // Simula que el RUT no existe aún y que save retorna el cliente guardado
        when(clienteRepository.findByRut("12345678-9")).thenReturn(Optional.empty());
        when(clienteRepository.save(cliente)).thenReturn(cliente);

        Cliente saved = clienteService.saveCliente(cliente);

        // Verifica que el cliente guardado sea el correcto
        assertNotNull(saved);
        assertEquals("12345678-9", saved.getRut());
        verify(clienteRepository, times(1)).save(cliente);
    }

    @Test
    public void testSaveCliente_rutDuplicado() {
        // Simula que el RUT ya existe en el sistema
        when(clienteRepository.findByRut("12345678-9")).thenReturn(Optional.of(cliente));

        // Verifica que se lanza RuntimeException por RUT duplicado
        assertThrows(RuntimeException.class, () -> clienteService.saveCliente(cliente));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    public void testDeleteCliente() {
        // El cliente existe y se elimina correctamente
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        doNothing().when(clienteRepository).deleteById(1);

        clienteService.deleteCliente(1);

        // Verifica que deleteById se haya llamado una vez
        verify(clienteRepository, times(1)).deleteById(1);
    }

    @Test
    public void testDeleteCliente_noExiste() {
        // Simula que el cliente a eliminar no existe
        when(clienteRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> clienteService.deleteCliente(99));
        verify(clienteRepository, never()).deleteById(any());
    }
}
