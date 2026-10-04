package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.ItemPedido;
import com.raizesdonordeste.api.repository.ItemPedidoRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-pedido")
public class ItemPedidoController {

    private final ItemPedidoRepository itemPedidoRepository;

    public ItemPedidoController(ItemPedidoRepository itemPedidoRepository) {
        this.itemPedidoRepository = itemPedidoRepository;
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<ItemPedido> listarPorPedido(@PathVariable Long pedidoId) {
        return itemPedidoRepository.findByPedido_Id(pedidoId);
    }
}