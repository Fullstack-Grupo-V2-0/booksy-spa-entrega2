package com.example.booksyspa.integration;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthClient {

    private final RestClient restClient;

    public AuthClient(@Value("${usuario-service.url}") String usuarioServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(usuarioServiceUrl).build();
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> login(String email, String password) {
        return this.restClient.post()
                .uri("auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Map.of("email", email, "password", password))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RuntimeException("Credenciales inválidas");
                })
                .body(Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> verificarUsuarioPorEmail(String email) {
        return this.restClient.get()
                .uri("usuarios/email/{email}", email)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new RuntimeException("Usuario no encontrado con email: " + email);
                })
                .body(Map.class);
    }
}
