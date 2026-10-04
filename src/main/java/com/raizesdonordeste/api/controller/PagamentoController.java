package com.raizesdonordeste.api.controller;

import com.raizesdonordeste.api.enums.StatusPagamento;
import com.raizesdonordeste.api.model.Pagamento;
import com.raizesdonordeste.api.repository.PagamentoRepository;
import com.raizesdonordeste.api.service.PagamentoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;
    private final PagamentoRepository pagamentoRepository;

    public PagamentoController(
            PagamentoService pagamentoService,
            PagamentoRepository pagamentoRepository
    ) {
        this.pagamentoService = pagamentoService;
        this.pagamentoRepository = pagamentoRepository;
    }

    @PostMapping("/pedido/{pedidoId}")
    public ResponseEntity<Pagamento> processarPagamento(
            @PathVariable Long pedidoId,
            @RequestParam StatusPagamento resultado
    ) {
        Pagamento pagamento =
                pagamentoService.processarPagamento(pedidoId, resultado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(pagamento);
    }

    @GetMapping("/pedido/{pedidoId}")
    public List<Pagamento> listarPorPedido(
            @PathVariable Long pedidoId
    ) {
        return pagamentoRepository.findByPedido_Id(pedidoId);
    }
}
