package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.model.ItemPedido;
import com.raizesdonordeste.api.repository.ItemPedidoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/itens-pedido")
public class ItemPedidoController {

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    @PostMapping
    public ResponseEntity<ItemPedido> adicionarItem(@Valid @RequestBody ItemPedido itemPedido) {
        ItemPedido novoItem = itemPedidoRepository.save(itemPedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoItem);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<ItemPedido> listarPorPedido(@PathVariable Long pedidoId) {
        return itemPedidoRepository.findByPedidoId(pedidoId);
    }
}