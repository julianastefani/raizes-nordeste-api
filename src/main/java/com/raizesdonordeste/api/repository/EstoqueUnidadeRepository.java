package com.raizesdonordeste.api.repository;

import com.raizesdonordeste.api.model.EstoqueUnidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EstoqueUnidadeRepository
        extends JpaRepository<EstoqueUnidade, Long> {

    Optional<EstoqueUnidade> findByUnidadeIdAndProdutoId(
            Long unidadeId,
            Long produtoId
    );
}
