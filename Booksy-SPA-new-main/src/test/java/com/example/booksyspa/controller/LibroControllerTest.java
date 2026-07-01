package com.example.booksyspa.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.booksyspa.assemblers.LibroAssembler;
import com.example.booksyspa.model.Autor;
import com.example.booksyspa.model.Categoria;
import com.example.booksyspa.model.Libro;
import com.example.booksyspa.service.LibroService;
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
public class LibroControllerTest {

    @Mock
    private LibroService libroService;

    @Mock
    private LibroAssembler libroAssembler;

    @InjectMocks
    private LibroController libroController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Libro libro;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converter.setSupportedMediaTypes(List.of(MediaTypes.HAL_JSON, MediaType.APPLICATION_JSON));

        mockMvc = MockMvcBuilders.standaloneSetup(libroController)
                .setMessageConverters(converter)
                .build();

        Autor autor = new Autor();
        autor.setIdAutor(1);
        autor.setNombre("Gabriel");
        autor.setApellido("García Márquez");

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1);
        categoria.setNombre("Ficción");

        libro = new Libro();
        libro.setIdLibro(1);
        libro.setIsbn("978-0-06-112008-4");
        libro.setTitulo("Cien años de soledad");
        libro.setAutor(autor);
        libro.setCategoria(categoria);
        libro.setPrecio(new BigDecimal("15000"));

        when(libroAssembler.toModel(any(Libro.class))).thenReturn(EntityModel.of(libro));
    }

    @Test
    public void testGetAllLibros() throws Exception {
        when(libroService.getLibros()).thenReturn(List.of(libro));

        mockMvc.perform(get("/api/v2/libros"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    public void testGetLibroById() throws Exception {
        when(libroService.getLibroId(1)).thenReturn(libro);

        mockMvc.perform(get("/api/v2/libros/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLibro").value(1))
                .andExpect(jsonPath("$.titulo").value("Cien años de soledad"))
                .andExpect(jsonPath("$.isbn").value("978-0-06-112008-4"));
    }

    @Test
    public void testGetLibroByIsbn() throws Exception {
        when(libroService.getLibroPorIsbn("978-0-06-112008-4")).thenReturn(libro);

        mockMvc.perform(get("/api/v2/libros/isbn/978-0-06-112008-4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titulo").value("Cien años de soledad"));
    }

    @Test
    public void testGetLibrosByCategoria() throws Exception {
        when(libroService.getLibrosPorCategoria(1)).thenReturn(List.of(libro));

        mockMvc.perform(get("/api/v2/libros/categoria/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    public void testGetLibrosByAutor() throws Exception {
        when(libroService.getLibrosPorAutor(1)).thenReturn(List.of(libro));

        mockMvc.perform(get("/api/v2/libros/autor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded").exists());
    }

    @Test
    public void testCreateLibro() throws Exception {
        when(libroService.saveLibro(any(Libro.class))).thenReturn(libro);

        mockMvc.perform(post("/api/v2/libros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libro)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.titulo").value("Cien años de soledad"));
    }

    @Test
    public void testUpdateLibro() throws Exception {
        when(libroService.updateLibro(anyInt(), any(Libro.class))).thenReturn(libro);

        mockMvc.perform(put("/api/v2/libros/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(libro)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idLibro").value(1));
    }

    @Test
    public void testDeleteLibro() throws Exception {
        doNothing().when(libroService).deleteLibro(1);

        mockMvc.perform(delete("/api/v2/libros/1"))
                .andExpect(status().isNoContent());

        verify(libroService, times(1)).deleteLibro(1);
    }
}
