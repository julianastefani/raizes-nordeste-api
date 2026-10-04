package com.raizesdonordeste.api.service;

import com.raizesdonordeste.api.dto.CriarPedidoRequest;
import com.raizesdonordeste.api.enums.StatusPedido;
import com.raizesdonordeste.api.exception.EstoqueInsuficienteException;
import com.raizesdonordeste.api.exception.RecursoNaoEncontradoException;
import com.raizesdonordeste.api.exception.TransicaoStatusInvalidaException;
import com.raizesdonordeste.api.model.ItemPedido;
import com.raizesdonordeste.api.model.Pedido;
import com.raizesdonordeste.api.model.Produto;
import com.raizesdonordeste.api.repository.ItemPedidoRepository;
import com.raizesdonordeste.api.repository.PedidoRepository;
import com.raizesdonordeste.api.repository.ProdutoRepository;
import com.raizesdonordeste.api.repository.UnidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;
    private final ItemPedidoRepository itemPedidoRepository;
    private final UnidadeRepository unidadeRepository;
    private final EstoqueService estoqueService;
    private final AuditoriaService auditoriaService;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ProdutoRepository produtoRepository,
            ItemPedidoRepository itemPedidoRepository,
            UnidadeRepository unidadeRepository,
            EstoqueService estoqueService,
            AuditoriaService auditoriaService
    ) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
        this.itemPedidoRepository = itemPedidoRepository;
        this.unidadeRepository = unidadeRepository;
        this.estoqueService = estoqueService;
        this.auditoriaService = auditoriaService;
    }

    @Transactional
    public Pedido criarPedido(CriarPedidoRequest request) {

        if (!unidadeRepository.existsById(request.getUnidadeId())) {
            throw new RecursoNaoEncontradoException(
                    "Unidade não encontrada."
            );
        }

        BigDecimal valorTotal = BigDecimal.ZERO;

        // Primeiro valida todos os produtos, estoque e calcula o total.
        for (CriarPedidoRequest.ItemRequest itemRequest : request.getItens()) {

            Produto produto = produtoRepository
                    .findById(itemRequest.getProdutoId())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Produto não encontrado."
                            )
                    );

            var estoque = estoqueService.buscarEstoque(
                    request.getUnidadeId(),
                    produto.getId()
            );

            if (estoque.getQuantidade() < itemRequest.getQuantidade()) {
                throw new EstoqueInsuficienteException(
                        "Estoque insuficiente para o produto: "
                                + produto.getNome()
                );
            }

            BigDecimal subtotal = produto
                    .getPreco()
                    .multiply(
                            BigDecimal.valueOf(itemRequest.getQuantidade())
                    );

            valorTotal = valorTotal.add(subtotal);
        }

        // Somente depois das validações o pedido é criado.
        Pedido pedido = new Pedido();

        pedido.setClienteId(request.getClienteId());
        pedido.setUnidadeId(request.getUnidadeId());
        pedido.setCanalPedido(request.getCanalPedido());
        pedido.setStatus(StatusPedido.AGUARDANDO_PAGAMENTO);
        pedido.setValorTotal(valorTotal);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Cria os itens e realiza a baixa no estoque.
        for (CriarPedidoRequest.ItemRequest itemRequest : request.getItens()) {

            Produto produto = produtoRepository
                    .findById(itemRequest.getProdutoId())
                    .orElseThrow(() ->
                            new RecursoNaoEncontradoException(
                                    "Produto não encontrado."
                            )
                    );

            ItemPedido itemPedido = new ItemPedido();

            itemPedido.setPedido(pedidoSalvo);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemRequest.getQuantidade());
            itemPedido.setPrecoUnitario(produto.getPreco());

            itemPedidoRepository.save(itemPedido);

            estoqueService.baixarEstoque(
                    request.getUnidadeId(),
                    produto.getId(),
                    itemRequest.getQuantidade()
            );
        }

        // Registra a criação do pedido na auditoria.
        auditoriaService.registrar(
                "CRIAR_PEDIDO",
                "PEDIDO",
                pedidoSalvo.getId()
        );

        return pedidoSalvo;
    }

    @Transactional
    public Pedido atualizarStatus(
            Long pedidoId,
            StatusPedido novoStatus
    ) {

        Pedido pedido = pedidoRepository
                .findById(pedidoId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException(
                                "Pedido não encontrado."
                        )
                );

        StatusPedido statusAtual = pedido.getStatus();

        if (!transicaoPermitida(statusAtual, novoStatus)) {
            throw new TransicaoStatusInvalidaException(
                    "Não é permitido alterar o pedido de "
                            + statusAtual
                            + " para "
                            + novoStatus
                            + "."
            );
        }

        pedido.setStatus(novoStatus);

        Pedido pedidoSalvo = pedidoRepository.save(pedido);

        // Registra a alteração de status na auditoria.
        auditoriaService.registrar(
                "ALTERAR_STATUS_PEDIDO",
                "PEDIDO",
                pedidoSalvo.getId()
        );

        return pedidoSalvo;
    }

    private boolean transicaoPermitida(
            StatusPedido statusAtual,
            StatusPedido novoStatus
    ) {

        return switch (statusAtual) {

            case PAGO ->
                    novoStatus == StatusPedido.EM_PREPARACAO;

            case EM_PREPARACAO ->
                    novoStatus == StatusPedido.PRONTO;

            case PRONTO ->
                    novoStatus == StatusPedido.ENTREGUE;

            default -> false;
        };
    }
}