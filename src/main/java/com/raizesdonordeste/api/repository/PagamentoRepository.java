package com.raizesdonordeste.api.repository;

import com.raizesdonordeste.api.model.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByPedido_Id(Long pedidoId);
}
