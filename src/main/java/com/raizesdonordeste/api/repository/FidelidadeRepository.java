package com.raizesdonordeste.api.repository;

import com.raizesdonordeste.api.model.Fidelidade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FidelidadeRepository extends JpaRepository<Fidelidade, Long> {

    Optional<Fidelidade> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioId(Long usuarioId);
}