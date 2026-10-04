package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.Promocao;
import com.raizesdonordeste.api.service.PromocaoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/promocoes")
public class PromocaoController {

    private final PromocaoService promocaoService;

    public PromocaoController(PromocaoService promocaoService) {
        this.promocaoService = promocaoService;
    }

    @PostMapping
    public ResponseEntity<Promocao> criar(
            @Valid @RequestBody Promocao promocao
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(promocaoService.criar(promocao));
    }

    @GetMapping
    public ResponseEntity<List<Promocao>> listar() {
        return ResponseEntity.ok(promocaoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Promocao> buscarPorId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                promocaoService.buscarPorId(id)
        );
    }

    @GetMapping("/ativas")
    public ResponseEntity<List<Promocao>> listarAtivas() {
        return ResponseEntity.ok(
                promocaoService.listarAtivas()
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Promocao> alterarStatus(
            @PathVariable Long id,
            @RequestParam Boolean ativa
    ) {
        return ResponseEntity.ok(
                promocaoService.alterarStatus(id, ativa)
        );
    }
}