package com.example.msusuarios.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.msusuarios.assemblers.UsuarioAssemblers;
import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.service.UsuarioService;
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
public class UsuarioControllerTest {

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private UsuarioAssemblers assemblers;

    @InjectMocks
    private UsuarioController usuarioController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(usuarioController)
                .setMessageConverters(converter)
                .build();

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Admin Test");
        usuario.setEmail("admin@test.cl");
        usuario.setPassword("1234");
        usuario.setRol("ADMIN");
        usuario.setActivo(1);

        when(assemblers.toModel(any(Usuario.class))).thenReturn(EntityModel.of(usuario));
    }

    @Test
    public void testGetAllUsuarios() throws Exception {
        when(usuarioService.getUsuarios()).thenReturn(List.of(usuario));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    public void testGetUsuarioById() throws Exception {
        when(usuarioService.getUsuarioById(1)).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.email").value("admin@test.cl"));
    }

    @Test
    public void testGetUsuarioByEmail() throws Exception {
        when(usuarioService.getUsuarioByEmail("admin@test.cl")).thenReturn(usuario);

        mockMvc.perform(get("/api/v1/usuarios/email/admin@test.cl"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Admin Test"));
    }

    @Test
    public void testCreateUsuario() throws Exception {
        when(usuarioService.saveUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("admin@test.cl"));
    }

    @Test
    public void testUpdateUsuario() throws Exception {
        when(usuarioService.updateUsuario(anyInt(), any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(usuario)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idUsuario").value(1));
    }

    @Test
    public void testDeleteUsuario() throws Exception {
        doNothing().when(usuarioService).deleteUsuario(1);

        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());

        verify(usuarioService, times(1)).deleteUsuario(1);
    }
}
