package com.example.booksyspa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.booksyspa.assemblers.PedidoAssembler;
import com.example.booksyspa.model.Cliente;
import com.example.booksyspa.model.EstadoPedido;
import com.example.booksyspa.model.Libro;
import com.example.booksyspa.model.Pedido;
import com.example.booksyspa.service.PedidoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class PedidoControllerTest {

    @Mock
    private PedidoService pedidoService;

    @Mock
    private PedidoAssembler pedidoAssembler;

    @InjectMocks
    private PedidoController pedidoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Pedido pedido;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(pedidoController)
                .setMessageConverters(converter)
                .build();

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setRut("12345678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setEmail("juan@test.cl");

        Libro libro = new Libro();
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

        lenient().when(pedidoAssembler.toModel(any(Pedido.class))).thenReturn(EntityModel.of(pedido));
    }

    @Test
    public void testGetAllPedidos() throws Exception {
        when(pedidoService.getPedidos()).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/v2/pedidos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testGetPedidoById() throws Exception {
        when(pedidoService.getPedidoId(1)).thenReturn(pedido);

        mockMvc.perform(get("/api/v2/pedidos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1))
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    public void testGetPedidosByCliente() throws Exception {
        when(pedidoService.getPedidosPorCliente(1)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/v2/pedidos/cliente/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testGetPedidosByLibro() throws Exception {
        when(pedidoService.getPedidosPorLibro(1)).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/v2/pedidos/libro/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testGetPedidosByEstado() throws Exception {
        when(pedidoService.getPedidosPorEstado("PENDIENTE")).thenReturn(List.of(pedido));

        mockMvc.perform(get("/api/v2/pedidos/estado").param("estado", "PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testCreatePedido() throws Exception {
        when(pedidoService.savePedido(any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(post("/api/v2/pedidos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    public void testUpdatePedido() throws Exception {
        when(pedidoService.updatePedido(anyInt(), any(Pedido.class))).thenReturn(pedido);

        mockMvc.perform(put("/api/v2/pedidos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pedido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPedido").value(1));
    }

    @Test
    public void testDeletePedido() throws Exception {
        doNothing().when(pedidoService).deletePedido(1);

        mockMvc.perform(delete("/api/v2/pedidos/1"))
                .andExpect(status().isNoContent());

        verify(pedidoService, times(1)).deletePedido(1);
    }
}
