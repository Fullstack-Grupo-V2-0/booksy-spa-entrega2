package com.example.booksyspa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.booksyspa.assemblers.AutorAssembler;
import com.example.booksyspa.model.Autor;
import com.example.booksyspa.service.AutorService;
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

import java.time.LocalDate;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class AutorControllerTest {

    @Mock
    private AutorService autorService;

    @Mock
    private AutorAssembler autorAssembler;

    @InjectMocks
    private AutorController autorController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Autor autor;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(autorController)
                .setMessageConverters(converter)
                .build();

        autor = new Autor();
        autor.setIdAutor(1);
        autor.setNombre("Gabriel");
        autor.setApellido("García Márquez");
        autor.setFechaNacimiento(LocalDate.of(1927, 3, 6));
        autor.setNacionalidad("Colombiana");

        lenient().when(autorAssembler.toModel(any(Autor.class))).thenReturn(EntityModel.of(autor));
    }

    @Test
    public void testGetAllAutores() throws Exception {
        when(autorService.obtenerAutores()).thenReturn(List.of(autor));

        mockMvc.perform(get("/api/v2/autores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").exists());
    }

    @Test
    public void testGetAutorById() throws Exception {
        when(autorService.buscarAutorPorId(1)).thenReturn(autor);

        mockMvc.perform(get("/api/v2/autores/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAutor").value(1))
                .andExpect(jsonPath("$.nombre").value("Gabriel"))
                .andExpect(jsonPath("$.apellido").value("García Márquez"));
    }

    @Test
    public void testCreateAutor() throws Exception {
        when(autorService.guardar(any(Autor.class))).thenReturn(autor);

        mockMvc.perform(post("/api/v2/autores")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autor)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Gabriel"));
    }

    @Test
    public void testUpdateAutor() throws Exception {
        when(autorService.actualizar(anyInt(), any(Autor.class))).thenReturn(autor);

        mockMvc.perform(put("/api/v2/autores/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(autor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAutor").value(1));
    }

    @Test
    public void testDeleteAutor() throws Exception {
        doNothing().when(autorService).eliminar(1);

        mockMvc.perform(delete("/api/v2/autores/1"))
                .andExpect(status().isNoContent());

        verify(autorService, times(1)).eliminar(1);
    }
}
