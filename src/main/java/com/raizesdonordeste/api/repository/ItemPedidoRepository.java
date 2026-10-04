package com.raizesdonordeste.api.repository;

import com.raizesdonordeste.api.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Long> {

    List<ItemPedido> findByPedido_Id(Long pedidoId);
}