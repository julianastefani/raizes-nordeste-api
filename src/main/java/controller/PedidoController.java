package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.repository.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    @Autowired
    private PedidoRepository pedidoRepository;

    // Endpoint para criar um pedido (exigindo o canalPedido obrigatório)
    @PostMapping
    public ResponseEntity<com.raizesdonordeste.api.model.pedido> criarPedido(@Valid @RequestBody com.raizesdonordeste.api.model.pedido pedido) {
        com.raizesdonordeste.api.model.pedido novoPedido = pedidoRepository.save(pedido);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoPedido);
    }

    // Endpoint para listar todos os pedidos ou filtrar por canal (ex: /pedidos?canalPedido=TOTEM)
    @GetMapping
    public List<com.raizesdonordeste.api.model.pedido> listarPedidos(@RequestParam(required = false) String canalPedido) {
        if (canalPedido != null && !canalPedido.isEmpty()) {
            return pedidoRepository.findByCanalPedido(canalPedido);
        }
        return pedidoRepository.findAll();
    }
}