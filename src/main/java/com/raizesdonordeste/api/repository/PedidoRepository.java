package com.raizesdonordeste.api.repository;

import com.raizesdonordeste.api.enums.CanalPedido;
import com.raizesdonordeste.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByCanalPedido(CanalPedido canalPedido);
}