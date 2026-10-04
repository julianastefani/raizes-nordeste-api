package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.enums.StatusPagamento;
import com.raizesdonordeste.api.enums.StatusPedido;
import com.raizesdonordeste.api.exception.RecursoNaoEncontradoException;
import com.raizesdonordeste.api.model.Pagamento;
import com.raizesdonordeste.api.model.Pedido;
import com.raizesdonordeste.api.repository.PagamentoRepository;
import com.raizesdonordeste.api.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PagamentoService {

    private final PagamentoRepository pagamentoRepository;
    private final PedidoRepository pedidoRepository;

    public PagamentoService(
            PagamentoRepository pagamentoRepository,
            PedidoRepository pedidoRepository
    ) {
        this.pagamentoRepository = pagamentoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Pagamento processarPagamento(
            Long pedidoId,
            StatusPagamento resultado
    ) {

        Pedido pedido = pedidoRepository
                .findById(pedidoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Pedido não encontrado."
                        )
                );

        if (pedido.getStatus() != StatusPedido.AGUARDANDO_PAGAMENTO
                && pedido.getStatus() != StatusPedido.PAGAMENTO_RECUSADO) {

            throw new IllegalStateException(
                    "O pedido não está aguardando pagamento."
            );
        }

        Pagamento pagamento = new Pagamento();

        pagamento.setPedido(pedido);
        pagamento.setValor(pedido.getValorTotal());
        pagamento.setStatus(resultado);

        if (resultado == StatusPagamento.APROVADO) {

            pedido.setStatus(StatusPedido.PAGO);

        } else {

            pedido.setStatus(StatusPedido.PAGAMENTO_RECUSADO);
        }

        pedidoRepository.save(pedido);

        return pagamentoRepository.save(pagamento);
    }
}
