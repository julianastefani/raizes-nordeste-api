package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.dto.CriarPedidoRequest;
import com.raizesdonordeste.api.enums.CanalPedido;
import com.raizesdonordeste.api.enums.StatusPedido;
import com.raizesdonordeste.api.model.Pedido;
import com.raizesdonordeste.api.repository.PedidoRepository;
import com.raizesdonordeste.api.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final PedidoService pedidoService;

    public PedidoController(
            PedidoRepository pedidoRepository,
            PedidoService pedidoService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<Pedido> criarPedido(
            @Valid @RequestBody CriarPedidoRequest request
    ) {
        Pedido novoPedido = pedidoService.criarPedido(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(novoPedido);
    }

    @GetMapping
    public List<Pedido> listarPedidos(
            @RequestParam(required = false) CanalPedido canalPedido
    ) {
        if (canalPedido != null) {
            return pedidoRepository.findByCanalPedido(canalPedido);
        }

        return pedidoRepository.findAll();
    }

    @PatchMapping("/{pedidoId}/status")
    public ResponseEntity<Pedido> atualizarStatus(
            @PathVariable Long pedidoId,
            @RequestParam StatusPedido status
    ) {
        Pedido pedidoAtualizado =
                pedidoService.atualizarStatus(pedidoId, status);

        return ResponseEntity.ok(pedidoAtualizado);
    }
}