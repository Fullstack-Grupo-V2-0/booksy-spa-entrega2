package com.example.booksyspa.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.booksyspa.exception.ResourceNotFoundException;
import com.example.booksyspa.model.*;
import com.example.booksyspa.repository.ClienteRepository;
import com.example.booksyspa.repository.LibroRepository;
import com.example.booksyspa.repository.PedidoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PedidoServiceTest {

    @Autowired
    private PedidoService pedidoService;

    @MockitoBean
    private PedidoRepository pedidoRepository;

    @MockitoBean
    private LibroRepository libroRepository;

    @MockitoBean
    private ClienteRepository clienteRepository;

    private Cliente cliente;
    private Libro libro;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        // Configura las entidades de prueba antes de cada test
        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setRut("12345678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");

        libro = new Libro();
        libro.setIdLibro(1);
        libro.setIsbn("978-0-06-112008-4");
        libro.setTitulo("Cien años de soledad");
        libro.setPrecio(new BigDecimal("15000"));

        pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setCliente(cliente);
        pedido.setLibro(libro);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(new BigDecimal("15000"));
    }

    @Test
    public void testGetPedidos() {
        // Define que el repositorio retorna una lista con un pedido
        when(pedidoRepository.findAll()).thenReturn(List.of(pedido));

        List<Pedido> result = pedidoService.getPedidos();

        // Verifica que la lista no sea nula y tenga exactamente un pedido
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(EstadoPedido.PENDIENTE, result.get(0).getEstado());
    }

    @Test
    public void testGetPedidoId_existe() {
        // Define que el pedido con id 1 existe
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

        Pedido found = pedidoService.getPedidoId(1);

        // Verifica que el pedido devuelto sea el correcto
        assertNotNull(found);
        assertEquals(1, found.getIdPedido());
        assertEquals(EstadoPedido.PENDIENTE, found.getEstado());
    }

    @Test
    public void testGetPedidoId_noExiste() {
        // Simula que el pedido con id 99 no existe
        when(pedidoRepository.findById(99)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.getPedidoId(99));
    }

    @Test
    public void testSavePedido() {
        // Simula que cliente y libro existen, y save retorna el pedido guardado
        when(clienteRepository.findById(1)).thenReturn(Optional.of(cliente));
        when(libroRepository.findById(1)).thenReturn(Optional.of(libro));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        Pedido saved = pedidoService.savePedido(pedido);

        // Verifica que el pedido guardado tenga los campos correctos que el servicio asigna
        assertNotNull(saved);
        assertEquals(EstadoPedido.PENDIENTE, saved.getEstado());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    public void testSavePedido_clienteNoExiste() {
        // Simula que el cliente del pedido no existe
        when(clienteRepository.findById(1)).thenReturn(Optional.empty());

        // Verifica que se lanza ResourceNotFoundException
        assertThrows(ResourceNotFoundException.class, () -> pedidoService.savePedido(pedido));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    public void testGetPedidosPorCliente() {
        // El cliente existe y tiene pedidos
        when(clienteRepository.existsById(1)).thenReturn(true);
        when(pedidoRepository.findByClienteIdCliente(1)).thenReturn(List.of(pedido));

        List<Pedido> result = pedidoService.getPedidosPorCliente(1);

        // Verifica que la lista tenga exactamente un pedido
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetPedidosPorCliente_clienteNoExiste() {
        // El cliente no existe: el servicio retorna lista vacía
        when(clienteRepository.existsById(99)).thenReturn(false);

        List<Pedido> result = pedidoService.getPedidosPorCliente(99);

        // Verifica que se retorna lista vacía
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testGetPedidosPorEstado_estadoValido() {
        // Define que el repositorio retorna pedidos con estado PENDIENTE
        when(pedidoRepository.findByEstado(EstadoPedido.PENDIENTE)).thenReturn(List.of(pedido));

        List<Pedido> result = pedidoService.getPedidosPorEstado("PENDIENTE");

        // Verifica que la lista tenga exactamente un pedido
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    public void testGetPedidosPorEstado_estadoInvalido() {
        // El servicio retorna lista vacía cuando el estado no es válido
        List<Pedido> result = pedidoService.getPedidosPorEstado("ESTADO_INVALIDO");

        // Verifica que se retorna lista vacía sin lanzar excepción
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void testDeletePedido() {
        // El pedido existe y se elimina correctamente
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        doNothing().when(pedidoRepository).deleteById(1);

        pedidoService.deletePedido(1);

        // Verifica que deleteById se haya llamado una vez
        verify(pedidoRepository, times(1)).deleteById(1);
    }
}
