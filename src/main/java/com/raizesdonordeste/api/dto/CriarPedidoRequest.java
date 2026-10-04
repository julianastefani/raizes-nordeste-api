package com.raizesdonordeste.api.dto;

import com.raizesdonordeste.api.enums.CanalPedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

public class CriarPedidoRequest {

    @NotNull(message = "O cliente é obrigatório.")
    private Long clienteId;

    @NotNull(message = "A unidade é obrigatória.")
    private Long unidadeId;

    @NotNull(message = "O canal do pedido é obrigatório.")
    private CanalPedido canalPedido;

    @Valid
    @NotEmpty(message = "O pedido deve possuir pelo menos um item.")
    private List<ItemRequest> itens;

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getUnidadeId() {
        return unidadeId;
    }

    public void setUnidadeId(Long unidadeId) {
        this.unidadeId = unidadeId;
    }

    public CanalPedido getCanalPedido() {
        return canalPedido;
    }

    public void setCanalPedido(CanalPedido canalPedido) {
        this.canalPedido = canalPedido;
    }

    public List<ItemRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemRequest> itens) {
        this.itens = itens;
    }

    public static class ItemRequest {

        @NotNull(message = "O produto é obrigatório.")
        private Long produtoId;

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        private Integer quantidade;

        public Long getProdutoId() {
            return produtoId;
        }

        public void setProdutoId(Long produtoId) {
            this.produtoId = produtoId;
        }

        public Integer getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(Integer quantidade) {
            this.quantidade = quantidade;
        }
    }
}