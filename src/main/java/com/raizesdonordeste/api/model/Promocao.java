package com.raizesdonordeste.api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promocoes")
public class Promocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome da promoção é obrigatório.")
    @Column(nullable = false)
    private String nome;

    private String descricao;

    @NotNull(message = "O percentual de desconto é obrigatório.")
    @DecimalMin(value = "0.01", message = "O desconto deve ser maior que zero.")
    @DecimalMax(value = "100.00", message = "O desconto não pode ser maior que 100%.")
    @Column(nullable = false)
    private BigDecimal percentualDesconto;

    @NotNull(message = "A data inicial é obrigatória.")
    @Column(nullable = false)
    private LocalDateTime dataInicio;

    @NotNull(message = "A data final é obrigatória.")
    @Column(nullable = false)
    private LocalDateTime dataFim;

    @Column(nullable = false)
    private Boolean ativa = true;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getPercentualDesconto() {
        return percentualDesconto;
    }

    public void setPercentualDesconto(BigDecimal percentualDesconto) {
        this.percentualDesconto = percentualDesconto;
    }

    public LocalDateTime getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDateTime getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }
}
