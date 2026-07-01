package com.example.booksyspa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.booksyspa.assemblers.ClienteAssembler;
import com.example.booksyspa.model.Cliente;
import com.example.booksyspa.service.ClienteService;
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

import java.util.List;

@ExtendWith(MockitoExtension.class)
public class ClienteControllerTest {

    @Mock
    private ClienteService clienteService;

    @Mock
    private ClienteAssembler clienteAssembler;

    @InjectMocks
    private ClienteController clienteController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Cliente cliente;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(clienteController)
                .setMessageConverters(converter)
                .build();

        cliente = new Cliente();
        cliente.setIdCliente(1);
        cliente.setRut("12345678-9");
        cliente.setNombre("Juan");
        cliente.setApellido("Pérez");
        cliente.setEmail("juan@test.cl");
        cliente.setTelefono("912345678");

        when(clienteAssembler.toModel(any(Cliente.class))).thenReturn(EntityModel.of(cliente));
    }

    @Test
    public void testGetAllClientes() throws Exception {
        when(clienteService.getClientes()).thenReturn(List.of(cliente));

        mockMvc.perform(get("/api/v2/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    public void testGetClienteById() throws Exception {
        when(clienteService.getClienteId(1)).thenReturn(cliente);

        mockMvc.perform(get("/api/v2/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1))
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    public void testGetClienteByRut() throws Exception {
        when(clienteService.getClientePorRut("12345678-9")).thenReturn(cliente);

        mockMvc.perform(get("/api/v2/clientes/rut/12345678-9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    @Test
    public void testCreateCliente() throws Exception {
        when(clienteService.saveCliente(any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(post("/api/v2/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.rut").value("12345678-9"));
    }

    @Test
    public void testUpdateCliente() throws Exception {
        when(clienteService.updateCliente(anyInt(), any(Cliente.class))).thenReturn(cliente);

        mockMvc.perform(put("/api/v2/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cliente)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1));
    }

    @Test
    public void testDeleteCliente() throws Exception {
        doNothing().when(clienteService).deleteCliente(1);

        mockMvc.perform(delete("/api/v2/clientes/1"))
                .andExpect(status().isNoContent());

        verify(clienteService, times(1)).deleteCliente(1);
    }
}
