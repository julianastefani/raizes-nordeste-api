package com.raizesdonordeste.api.repository;

import com.raizesdonordeste.api.model.Promocao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromocaoRepository extends JpaRepository<Promocao, Long> {

    List<Promocao> findByAtivaTrue();
}
