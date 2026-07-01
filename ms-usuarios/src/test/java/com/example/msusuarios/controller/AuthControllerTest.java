package com.example.msusuarios.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.repository.UsuarioRepository;
import com.example.msusuarios.security.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Map;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

        usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setNombre("Admin Test");
        usuario.setEmail("admin@test.cl");
        usuario.setPassword("1234");
        usuario.setRol("ADMIN");
        usuario.setActivo(1);
    }

    @Test
    public void testLogin_exitoso() throws Exception {
        when(usuarioRepository.findByEmail("admin@test.cl")).thenReturn(Optional.of(usuario));
        when(jwtUtil.generateToken("admin@test.cl", "ADMIN")).thenReturn("token-simulado");

        String requestBody = objectMapper.writeValueAsString(
                Map.of("email", "admin@test.cl", "password", "1234"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-simulado"))
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.email").value("admin@test.cl"))
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    public void testLogin_usuarioNoExiste() throws Exception {
        when(usuarioRepository.findByEmail("noexiste@test.cl")).thenReturn(Optional.empty());

        String requestBody = objectMapper.writeValueAsString(
                Map.of("email", "noexiste@test.cl", "password", "1234"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }

    @Test
    public void testLogin_contrasenaIncorrecta() throws Exception {
        when(usuarioRepository.findByEmail("admin@test.cl")).thenReturn(Optional.of(usuario));

        String requestBody = objectMapper.writeValueAsString(
                Map.of("email", "admin@test.cl", "password", "incorrecta"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }
}
