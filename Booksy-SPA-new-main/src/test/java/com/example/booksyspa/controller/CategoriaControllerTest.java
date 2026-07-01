package com.example.booksyspa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.booksyspa.assemblers.CategoriaAssembler;
import com.example.booksyspa.model.Categoria;
import com.example.booksyspa.service.CategoriaService;
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
public class CategoriaControllerTest {

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private CategoriaAssembler categoriaAssembler;

    @InjectMocks
    private CategoriaController categoriaController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Categoria categoria;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(categoriaController)
                .setMessageConverters(converter)
                .build();

        categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Ficción");
        categoria.setDescripcion("Novelas de ficción literaria");

        when(categoriaAssembler.toModel(any(Categoria.class))).thenReturn(EntityModel.of(categoria));
    }

    @Test
    public void testGetAllCategorias() throws Exception {
        when(categoriaService.getCategorias()).thenReturn(List.of(categoria));

        mockMvc.perform(get("/api/v2/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    public void testGetCategoriaById() throws Exception {
        when(categoriaService.getCategoriaId(1)).thenReturn(categoria);

        mockMvc.perform(get("/api/v2/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre").value("Ficción"));
    }

    @Test
    public void testCreateCategoria() throws Exception {
        when(categoriaService.saveCategoria(any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(post("/api/v2/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Ficción"));
    }

    @Test
    public void testUpdateCategoria() throws Exception {
        when(categoriaService.updateCategoria(anyInt(), any(Categoria.class))).thenReturn(categoria);

        mockMvc.perform(put("/api/v2/categorias/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(categoria)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1));
    }

    @Test
    public void testDeleteCategoria() throws Exception {
        doNothing().when(categoriaService).deleteCategoria(1);

        mockMvc.perform(delete("/api/v2/categorias/1"))
                .andExpect(status().isNoContent());

        verify(categoriaService, times(1)).deleteCategoria(1);
    }
}
