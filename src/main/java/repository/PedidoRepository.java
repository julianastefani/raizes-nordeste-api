package com.raizesdonordeste.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<com.raizesdonordeste.api.model.pedido, Long> {

    // Método para cumprir a exigência da Uninter: filtrar pedidos pelo canal (ex: TOTEM, APP)
    List<com.raizesdonordeste.api.model.pedido> findByCanalPedido(String canalPedido);
}