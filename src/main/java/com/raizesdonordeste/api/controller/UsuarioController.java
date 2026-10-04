package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.Usuario;
import com.raizesdonordeste.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<Usuario> cadastrar(
            @Valid @RequestBody Usuario usuario
    ) {

        Usuario usuarioSalvo =
                usuarioService.cadastrar(usuario);

        // Nunca devolver a senha na resposta da API.
        usuarioSalvo.setSenha(null);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioSalvo);
    }

    @PatchMapping("/{usuarioId}/anonimizar")
    public ResponseEntity<Usuario> anonimizar(
            @PathVariable Long usuarioId
    ) {

        Usuario usuarioAnonimizado =
                usuarioService.anonimizar(usuarioId);

        // Nunca devolver a senha na resposta da API.
        usuarioAnonimizado.setSenha(null);

        return ResponseEntity.ok(usuarioAnonimizado);
    }
}