package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.Unidade;
import com.raizesdonordeste.api.repository.UnidadeRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidades")
public class UnidadeController {

    private final UnidadeRepository unidadeRepository;

    public UnidadeController(UnidadeRepository unidadeRepository) {
        this.unidadeRepository = unidadeRepository;
    }

    @PostMapping
    public ResponseEntity<Unidade> criarUnidade(
            @Valid @RequestBody Unidade unidade
    ) {
        Unidade novaUnidade = unidadeRepository.save(unidade);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novaUnidade);
    }

    @GetMapping
    public List<Unidade> listarUnidades() {
        return unidadeRepository.findAll();
    }
}
