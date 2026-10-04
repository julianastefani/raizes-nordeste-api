package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.exception.EstoqueInsuficienteException;
import com.raizesdonordeste.api.exception.RecursoNaoEncontradoException;
import com.raizesdonordeste.api.model.EstoqueUnidade;
import com.raizesdonordeste.api.repository.EstoqueUnidadeRepository;
import org.springframework.stereotype.Service;

@Service
public class EstoqueService {

    private final EstoqueUnidadeRepository estoqueUnidadeRepository;

    public EstoqueService(EstoqueUnidadeRepository estoqueUnidadeRepository) {
        this.estoqueUnidadeRepository = estoqueUnidadeRepository;
    }

    public EstoqueUnidade buscarEstoque(Long unidadeId, Long produtoId) {
        return estoqueUnidadeRepository
                .findByUnidadeIdAndProdutoId(unidadeId, produtoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Produto não encontrado no estoque desta unidade."
                        )
                );
    }

    public boolean possuiEstoque(
            Long unidadeId,
            Long produtoId,
            Integer quantidadeSolicitada
    ) {
        EstoqueUnidade estoque = buscarEstoque(unidadeId, produtoId);

        return estoque.getQuantidade() >= quantidadeSolicitada;
    }

    public EstoqueUnidade baixarEstoque(
            Long unidadeId,
            Long produtoId,
            Integer quantidadeSolicitada
    ) {

        EstoqueUnidade estoque = buscarEstoque(unidadeId, produtoId);

        if (quantidadeSolicitada == null || quantidadeSolicitada <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade solicitada deve ser maior que zero."
            );
        }

        if (estoque.getQuantidade() < quantidadeSolicitada) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para o produto solicitado."
            );
        }

        estoque.setQuantidade(
                estoque.getQuantidade() - quantidadeSolicitada
        );

        return estoqueUnidadeRepository.save(estoque);
    }
}