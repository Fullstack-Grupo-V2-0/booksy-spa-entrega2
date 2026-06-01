package com.example.msusuarios.controller;

import com.example.msusuarios.dto.LoginRequest;
import com.example.msusuarios.model.Usuario;
import com.example.msusuarios.repository.UsuarioRepository;
import com.example.msusuarios.security.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        log.info("POST /api/v1/auth/login - email={}", request.getEmail());

        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(request.getEmail());

        if (optUsuario.isEmpty() || !optUsuario.get().getPassword().equals(request.getPassword())) {
            log.warn("Login fallido para email={}", request.getEmail());
            return ResponseEntity.status(401).body(Map.of("mensaje", "Credenciales inválidas"));
        }

        Usuario u = optUsuario.get();
        String token = jwtUtil.generateToken(u.getEmail(), u.getRol());
        log.info("Login exitoso para email={}", u.getEmail());

        return ResponseEntity.ok(Map.of(
                "token", token,
                "tipo", "Bearer",
                "email", u.getEmail(),
                "rol", u.getRol()
        ));
    }
}
