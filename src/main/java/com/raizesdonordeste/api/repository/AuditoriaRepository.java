package com.raizesdonordeste.api.repository;

import com.raizesdonordeste.api.model.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    List<Auditoria> findByRecursoOrderByDataHoraDesc(String recurso);

    List<Auditoria> findAllByOrderByDataHoraDesc();
}