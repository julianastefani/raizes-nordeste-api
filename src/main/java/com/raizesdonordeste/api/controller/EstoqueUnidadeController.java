package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.EstoqueUnidade;
import com.raizesdonordeste.api.repository.EstoqueUnidadeRepository;
import com.raizesdonordeste.api.service.EstoqueService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/estoque")
public class EstoqueUnidadeController {

    private final EstoqueUnidadeRepository estoqueRepository;
    private final EstoqueService estoqueService;

    public EstoqueUnidadeController(
            EstoqueUnidadeRepository estoqueRepository,
            EstoqueService estoqueService
    ) {
        this.estoqueRepository = estoqueRepository;
        this.estoqueService = estoqueService;
    }

    @PostMapping
    public ResponseEntity<EstoqueUnidade> criar(
            @Valid @RequestBody EstoqueUnidade estoque
    ) {
        EstoqueUnidade novoEstoque = estoqueRepository.save(estoque);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoEstoque);
    }

    @GetMapping
    public List<EstoqueUnidade> listar() {
        return estoqueRepository.findAll();
    }

    @GetMapping("/unidade/{unidadeId}/produto/{produtoId}")
    public ResponseEntity<EstoqueUnidade> buscarEstoque(
            @PathVariable Long unidadeId,
            @PathVariable Long produtoId
    ) {
        EstoqueUnidade estoque =
                estoqueService.buscarEstoque(unidadeId, produtoId);

        return ResponseEntity.ok(estoque);
    }

    @PatchMapping("/unidade/{unidadeId}/produto/{produtoId}/baixa")
    public ResponseEntity<EstoqueUnidade> baixarEstoque(
            @PathVariable Long unidadeId,
            @PathVariable Long produtoId,
            @RequestParam Integer quantidade
    ) {
        EstoqueUnidade estoqueAtualizado =
                estoqueService.baixarEstoque(
                        unidadeId,
                        produtoId,
                        quantidade
                );

        return ResponseEntity.ok(estoqueAtualizado);
    }
}