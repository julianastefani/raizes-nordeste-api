package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.dto.LoginRequest;
import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.service.AuthService;
import com.raizesdonordeste.api.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    public AuthController(
            AuthService authService,
            JwtService jwtService
    ) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(
            @Valid @RequestBody LoginRequest request
    ) {

        Usuario usuario = authService.autenticar(request);

        String token = jwtService.gerarToken(usuario);

        Map<String, Object> dadosUsuario = new HashMap<>();

        dadosUsuario.put("id", usuario.getId());
        dadosUsuario.put("nome", usuario.getNome());
        dadosUsuario.put("email", usuario.getEmail());
        dadosUsuario.put("perfil", usuario.getPerfil());

        Map<String, Object> resposta = new HashMap<>();

        resposta.put("token", token);
        resposta.put("tipo", "Bearer");
        resposta.put("usuario", dadosUsuario);

        return ResponseEntity.ok(resposta);
    }
}
